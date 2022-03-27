package keyvaluestore

import akka.actor._
import scala.concurrent.duration._
import scala.language.postfixOps

object Replicator {
  case class Replicate(key: String, valueOption: Option[String], id: Long)
  case class Replicated(key: String, id: Long)

  case class Snapshot(key: String, valueOption: Option[String], seq: Long)
  case class SnapshotAck(key: String, seq: Long)

  def props(replica: ActorRef): Props = Props(new Replicator(replica))
}

class Replicator(val replica: ActorRef) extends Actor {

  import Replicator._
  import context.dispatcher

  // (SEQ -> (Sender, REPLICATE-MSG))
  var acks = Map.empty[Long, (ActorRef, Replicate)]
  // (SEQ -> Cancellable)
  var toCancel = Map.empty[Long, Cancellable]

  val scheduler: Scheduler = context.system.scheduler

  var _seqCounter = 0L
  def nextSeq: Long = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }

  def receive: Receive = {
    case Replicate(key, valueOption, id) =>
      val seq = nextSeq
      acks += (seq -> (sender(), Replicate(key, valueOption, id)))
      scheduleForSnapshot(key, valueOption, seq)

    case SnapshotAck(key, seq) =>
      acks.get(seq) match {
        case Some((sdr, Replicate(k, vO, id))) =>
          toCancel(seq).cancel()
          toCancel -= seq
          sdr ! Replicated(k, id)
      }
  }

  private def scheduleForSnapshot(k: String, vo: Option[String], seq: Long) {
    val cancellable =
      scheduler.schedule(Duration.Zero, 100 millis, replica, Snapshot(k, vo, seq))
    toCancel = toCancel +  (seq -> cancellable)
  }

}
