package keyvaluestore

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import keyvaluestore.Mediator.{Replicas, _}

import scala.concurrent.duration._
import scala.language.postfixOps

object Replica {

  sealed trait Operation {
    def key: String
    def id: Long
  }

  case class Insert(key: String, value: String, id: Long) extends Operation
  case class Remove(key: String, id: Long) extends Operation
  case class Get(key: String, id: Long) extends Operation

  sealed trait OperationReply
  case class OperationAck(id: Long) extends OperationReply
  case class OperationFailure(id: Long) extends OperationReply
  case class GetResult(key: String, valueOption: Option[String], id: Long) extends OperationReply

  def props(mediator: ActorRef, persistenceProps: Props): Props =
    Props(new Replica(mediator, persistenceProps))
}

class Replica(val mediator: ActorRef, persistenceProps: Props) extends Actor {

  import Persistence._
  import Replica._
  import Replicator._
  import context.dispatcher

  // All replicas send 'Join' to the mediator immediately upon instantiation
  mediator ! Join

  var kv = Map.empty[String, String]

  // (KEY -> ID) where ID=most recent update id for that key
  var ki = Map.empty[String, Long]
  // (SEC_REPLICA -> CORRESPONDING_REPLICATOR)
  var secondaries = Map.empty[ActorRef, ActorRef]
  // Set[Replicator]
  var replicators = Set.empty[ActorRef]

  // Conditions for OperationAck
  // 1. PERSISTENCE at current (i.e. primary) replica
  // Set[ID] of updates that are not yet persisted [PersAck <=> Persisted]
  var persAcks_due = Set.empty[Long]

  // 2. All sec replicas have ACKed the REPLICATION of update
  // (ID -> number of repAcks due/outstanding) [repAck <=> Replicated]
  var num_repAcks_due_by_id = Map.empty[Long, Int]
  // (Replicator -> Set[ID])
  var repAcks_due_by_replicator = Map.empty[ActorRef, Set[Long]]
  // (ID -> requester's ActorRef)
  var sender_by_id = Map.empty[Long, ActorRef]

  // local persistence object
  lazy val persistence: ActorRef = context.actorOf(persistenceProps)
  // Cancellators for persistence operations
  private var toCancelPers: Map[Long, (ActorRef, Cancellable)] = Map.empty

  val scheduler: Scheduler = context.system.scheduler

  def receive = {
    case JoinedPrimary => context.become(leader)
    case JoinedSecondary => context.become(replica)
  }

  /** *************************************************************
    * Primary replica
    * *************************************************************/

  val leader: Receive = {
    case Get(key: String, id: Long) => sender() ! GetResult(key, kv.get(key), id)

    case Persisted(key, id) =>
      toCancelPers.get(id) match {
        case Some((sdr, cancellable)) =>
          cancellable.cancel()
          toCancelPers -= id
          persAcks_due -= id
          if (!num_repAcks_due_by_id.contains(id)) sdr ! OperationAck(id)
      }

    case Insert(key: String, value: String, id: Long) =>
      sender_by_id += (id -> sender())
      persAcks_due += id
      kv += (key -> value)
      ki += (key -> id)

      scheduleForPersistence(key, Some(value), id)

      if (replicators.nonEmpty) {
        num_repAcks_due_by_id += (id -> replicators.size)
        replicators.foreach{ r =>
          repAcks_due_by_replicator += (r -> (repAcks_due_by_replicator(r)+id))
          r ! Replicate(key, Some(value), id)
        }
      }

      scheduler.scheduleOnce(1 second) {
        if (num_repAcks_due_by_id.contains(id) || persAcks_due.contains(id)) sender_by_id(id) ! OperationFailure(id)
      }

    case Remove(key: String, id: Long) =>
      sender_by_id += (id -> sender())
      persAcks_due += id

      kv -= key
      // ki -= key

      scheduleForPersistence(key, None, id)

      if (replicators.nonEmpty) {
        num_repAcks_due_by_id += (id -> replicators.size)
        replicators.foreach{ r =>
          repAcks_due_by_replicator += (r -> (repAcks_due_by_replicator(r)+id))
          r ! Replicate(key, None, id)
        }
      }

      scheduler.scheduleOnce(1 second) {
        if (num_repAcks_due_by_id.contains(id) || persAcks_due.contains(id)) sender_by_id(id) ! OperationFailure(id)
      }

    case Replicas(replicas) =>
      val updated_sec_replicas = replicas - self

      val new_sec_replicas: Set[ActorRef] = updated_sec_replicas -- secondaries.keySet
      new_sec_replicas.foreach { new_sec_replica =>
        val new_replicator = context.actorOf(Replicator.props(new_sec_replica))
        replicators += new_replicator
        secondaries += (new_sec_replica -> new_replicator)
        repAcks_due_by_replicator += (new_replicator -> Set.empty[Long])
        kv.foreach { key_val =>
          new_replicator ! Replicate(key_val._1, Some(key_val._2), ki(key_val._1))
        }
      }

      val removed_sec_replicas: Set[ActorRef] = secondaries.keySet -- updated_sec_replicas
      removed_sec_replicas.foreach { removed_sec_replica =>
        val corresponding_replicator = secondaries(removed_sec_replica)
        replicators -= corresponding_replicator
        repAcks_due_by_replicator(corresponding_replicator).foreach(id => self ! Replicated("k",id))
        repAcks_due_by_replicator -= corresponding_replicator
        corresponding_replicator ! PoisonPill
      }

    case Replicated(key: String, id: Long) =>
      if (num_repAcks_due_by_id.contains(id)) {
        if (sender() != self) {
          repAcks_due_by_replicator += (sender() -> (repAcks_due_by_replicator(sender())-id))
        }
        val outstanding_replicatedAck = num_repAcks_due_by_id(id) - 1
        if (outstanding_replicatedAck != 0) {
          num_repAcks_due_by_id += (id -> outstanding_replicatedAck)
        } else {
          num_repAcks_due_by_id -= id
          if (!persAcks_due.contains(id)) sender_by_id(id) ! OperationAck(id)
        }
      }
  }

  /** *************************************************************
    * Secondary replicas
    * *************************************************************/
  var expected_seq = 0L

  val replica: Receive = {
    case Get(key: String, id: Long) => sender() ! GetResult(key, kv.get(key), id)

    case Snapshot(key: String, valueOption: Option[String], seq: Long) =>
      if (seq < expected_seq) sender() ! SnapshotAck(key, seq)
      else if (seq > expected_seq) {}
      else {
        valueOption match {
          case None => kv -= key
          case Some(value) => kv += (key -> value)
        }
        scheduleForPersistence(key, valueOption, seq)
        expected_seq += 1
      }

    case Persisted(key, seq) =>
      toCancelPers.get(seq) match {
        case Some((sdr, cancellable)) =>
          cancellable.cancel()
          toCancelPers -= seq
          sdr ! SnapshotAck(key, seq)
      }
  }
  
  /** *************************************************************
    * Supervision of persistence nodes
    * *************************************************************/

  /**
    * Handling persistence failures by means of restarting the corresponding actor
    */
  override def supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _: PersistenceException => Restart
  }


  /** *************************************************************
    * Useful auxiliary methods for performing timed operations
    * *************************************************************/

  /**
    * Schedule a snapshot for persistence
    *
    * @param k a key 
    * @param vo and optional value
    * @param seq identified of the request
    */
  private def scheduleForPersistence(k: String, vo: Option[String], seq: Long) {
    val scheduler = context.system.scheduler
    val cancellable =
      scheduler.schedule(Duration.Zero, 100 millis, persistence, Persist(k, vo, seq))
    toCancelPers = toCancelPers + (seq -> (sender, cancellable))
  }

}
