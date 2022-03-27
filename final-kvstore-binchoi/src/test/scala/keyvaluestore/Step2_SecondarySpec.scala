package keyvaluestore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import keyvaluestore.Mediator.{Join, JoinedSecondary}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class Step2_SecondarySpec extends TestKit(ActorSystem("Step2SecondarySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Tools {

  override def afterAll(): Unit = {
    system.terminate()
  }

  "[02-Secondary]" must {

    "case 1: Secondary (in isolation) should properly register itself to the provided mediator" in {
      val mediator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case1-secondary")

      mediator.expectMsg(Join)
    }
    
    "case 2: Secondary (in isolation) must handle Snapshots" in {
      import Replicator._

      val mediator = TestProbe()
      val replicator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case2-secondary")
      val client = session(secondary)

      mediator.expectMsg(Join)
      mediator.send(secondary, JoinedSecondary)

      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", None, 0L))
      replicator.expectMsg(SnapshotAck("k1", 0L))
      
      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v1"), 1L))
      replicator.expectMsg(SnapshotAck("k1", 1L))
      assert(client.get("k1").contains("v1"))

      replicator.send(secondary, Snapshot("k1", None, 2L))
      replicator.expectMsg(SnapshotAck("k1", 2L))
      assert(client.get("k1").isEmpty)
    }


    "case 3: Secondary should drop and immediately ack snapshots with older sequence numbers" in {
      import Replicator._

      val mediator = TestProbe()
      val replicator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case3-secondary")
      val client = session(secondary)

      mediator.expectMsg(Join)
      mediator.send(secondary, JoinedSecondary)

      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v1"), 0L))
      replicator.expectMsg(SnapshotAck("k1", 0L))
      assert(client.get("k1").contains("v1"))

      replicator.send(secondary, Snapshot("k1", None, 0L))
      replicator.expectMsg(SnapshotAck("k1", 0L))
      assert(client.get("k1").contains("v1"))

      replicator.send(secondary, Snapshot("k1", Some("v2"), 1L))
      replicator.expectMsg(SnapshotAck("k1", 1L))
      assert(client.get("k1").contains("v2"))

      replicator.send(secondary, Snapshot("k1", None, 0L))
      replicator.expectMsg(SnapshotAck("k1", 0L))
      assert(client.get("k1").contains("v2"))
    }

    "case 4: Secondary should drop snapshots with future sequence numbers" in {
      import Replicator._

      val mediator = TestProbe()
      val replicator = TestProbe()
      val secondary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case4-secondary")
      val client = session(secondary)

      mediator.expectMsg(Join)
      mediator.send(secondary, JoinedSecondary)

      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v1"), 1L))
      replicator.expectNoMessage(300.milliseconds)
      assert(client.get("k1").isEmpty)

      replicator.send(secondary, Snapshot("k1", Some("v2"), 0L))
      replicator.expectMsg(SnapshotAck("k1", 0L))
      assert(client.get("k1").contains("v2"))
    }


  }



}