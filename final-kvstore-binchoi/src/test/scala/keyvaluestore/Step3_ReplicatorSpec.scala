package keyvaluestore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import keyvaluestore.Replicator.{Replicate, Snapshot, SnapshotAck}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class Step3_ReplicatorSpec extends TestKit(ActorSystem("Step3ReplicatorSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Tools {

  override def afterAll(): Unit = {
    system.terminate()
  }

  "[03-Replicator]" must {

    "case 1: Replicator should send snapshots when asked to replicate" in {
      val secondary = TestProbe()
      val replicator = system.actorOf(Replicator.props(secondary.ref), "case1-replicator")

      replicator ! Replicate("k1", Some("v1"), 0L)
      secondary.expectMsg(Snapshot("k1", Some("v1"), 0L))
      secondary.ignoreMsg({ case Snapshot(_, _, 0L) => true })
      secondary.reply(SnapshotAck("k1", 0L))

      replicator ! Replicate("k1", Some("v2"), 1L)
      secondary.expectMsg(Snapshot("k1", Some("v2"), 1L))
      secondary.ignoreMsg({ case Snapshot(_, _, 1L) => true })
      secondary.reply(SnapshotAck("k1", 1L))

      replicator ! Replicate("k2", Some("v1"), 2L)
      secondary.expectMsg(Snapshot("k2", Some("v1"), 2L))
      secondary.ignoreMsg({ case Snapshot(_, _, 2L) => true })
      secondary.reply(SnapshotAck("k2", 2L))

      replicator ! Replicate("k1", None, 3L)
      secondary.expectMsg(Snapshot("k1", None, 3L))
      secondary.reply(SnapshotAck("k1", 3L))
    }

    "case 2: Replicator should retry until acknowledged by secondary" in {
      val secondary = TestProbe()
      val replicator = system.actorOf(Replicator.props(secondary.ref), "case2-replicator")

      replicator ! Replicate("k1", Some("v1"), 0L)
      secondary.expectMsg(Snapshot("k1", Some("v1"), 0L))
      secondary.expectMsg(300.milliseconds, Snapshot("k1", Some("v1"), 0L))
      secondary.expectMsg(300.milliseconds, Snapshot("k1", Some("v1"), 0L))

      secondary.reply(SnapshotAck("k1", 0L))
    }

  }



}
