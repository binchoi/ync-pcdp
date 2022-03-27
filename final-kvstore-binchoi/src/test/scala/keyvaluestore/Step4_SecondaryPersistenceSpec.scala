package keyvaluestore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import keyvaluestore.Mediator.{Join, JoinedSecondary}
import keyvaluestore.Persistence.{Persist, Persisted}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class Step4_SecondaryPersistenceSpec extends TestKit(ActorSystem("Step4SecondaryPersistenceSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Tools {

  override def afterAll(): Unit = {
    system.terminate()
  }

  "[04-Secondary-Persistence]" must {
    
    "case 1: Secondary should not acknowledge snapshots until persisted" in {
      import Replicator._

      val mediator = TestProbe()
      val persistence = TestProbe()
      val replicator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, probeProps(persistence)), "case1-secondary")
      val client = session(secondary)

      mediator.expectMsg(Join)
      mediator.send(secondary, JoinedSecondary)

      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v1"), 0L))
      val persistId = persistence.expectMsgPF() {
        case Persist("k1", Some("v1"), id) => id
      }
      // Already serving...
      assert(client.get("k1").contains("v1"))

      replicator.expectNoMessage(500.milliseconds)

      persistence.reply(Persisted("k1", persistId))
      replicator.expectMsg(SnapshotAck("k1", 0L))
    }

    "case 2: Secondary should retry persistence in every 100 milliseconds" in {
      import Replicator._

      val mediator = TestProbe()
      val persistence = TestProbe()
      val replicator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, probeProps(persistence)), "case2-secondary")
      val client = session(secondary)

      mediator.expectMsg(Join)
      mediator.send(secondary, JoinedSecondary)

      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v1"), 0L))
      val persistId = persistence.expectMsgPF() {
        case Persist("k1", Some("v1"), id) => id
      }
      // Already serving...
      assert(client.get("k1").contains("v1"))

      // Persistence should be retried
      persistence.expectMsg(200.milliseconds, Persist("k1", Some("v1"), persistId))
      persistence.expectMsg(200.milliseconds, Persist("k1", Some("v1"), persistId))

      replicator.expectNoMessage(500.milliseconds)

      persistence.reply(Persisted("k1", persistId))
      replicator.expectMsg(SnapshotAck("k1", 0L))
    }    
  }



}