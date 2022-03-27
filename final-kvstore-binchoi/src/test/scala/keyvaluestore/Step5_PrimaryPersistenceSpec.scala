package keyvaluestore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import keyvaluestore.Mediator.{Join, JoinedPrimary, Replicas}
import keyvaluestore.Persistence.{Persist, Persisted}
import keyvaluestore.Replicator.{Snapshot, SnapshotAck}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class Step5_PrimaryPersistenceSpec extends TestKit(ActorSystem("Step5PrimaryPersistenceSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Tools {

  override def afterAll(): Unit = {
    system.terminate()
  }
  
  "[05-Primary-Persistence]" must {
    
    "case 1: Primary does not acknowledge updates which have not been persisted" in {
      val mediator = TestProbe()
      val persistence = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, probeProps(persistence)), "case1-primary")
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)

      val setId = client.set("foo", "bar")
      val persistId = persistence.expectMsgPF() {
        case Persist("foo", Some("bar"), id) => id
      }

      client.nothingHappens(100.milliseconds)
      persistence.reply(Persisted("foo", persistId))
      client.waitAck(setId)
    }
    
    "case 2: Primary retries persistence every 100 milliseconds" in {
      val mediator = TestProbe()
      val persistence = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, probeProps(persistence)), "case2-primary")
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)

      val setId = client.set("foo", "bar")
      val persistId = persistence.expectMsgPF() {
        case Persist("foo", Some("bar"), id) => id
      }
      // Retries form above
      persistence.expectMsg(200.milliseconds, Persist("foo", Some("bar"), persistId))
      persistence.expectMsg(200.milliseconds, Persist("foo", Some("bar"), persistId))

      client.nothingHappens(100.milliseconds)
      persistence.reply(Persisted("foor", persistId))
      client.waitAck(setId)
    }

    "case 3: Primary generates failure after 1 second if persistence fails" in {
      val mediator = TestProbe()
      val persistence = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, probeProps(persistence)), "case3-primary")
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)

      val setId = client.set("foo", "bar")
      persistence.expectMsgType[Persist]
      client.nothingHappens(800.milliseconds)  // Should not fail too early
      client.waitFailed(setId)
    }

    "case 4: Primary generates failure after 1 second if global acknowledgement fails" in {
      val mediator = TestProbe()
      val persistence = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case4-primary")
      val secondary = TestProbe()
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)
      mediator.send(primary, Replicas(Set(primary, secondary.ref)))

      val setId = client.set("foo", "bar")
      secondary.expectMsgType[Snapshot]
      client.nothingHappens(800.milliseconds) // SHould not fail too early
      client.waitFailed(setId)
    }

    "case 5: Primary acknowledges only after persistence and global acknowledgement" in {
      val mediator = TestProbe()
      val persistence = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = false)), "case5-primary")
      val secondaryA, secondaryB = TestProbe()
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)
      mediator.send(primary, Replicas(Set(primary, secondaryA.ref, secondaryB.ref)))

      val setId = client.set("foo", "bar")
      val seqA = secondaryA.expectMsgType[Snapshot].seq
      val seqB = secondaryB.expectMsgType[Snapshot].seq
      client.nothingHappens(300.milliseconds)
      secondaryA.reply(SnapshotAck("foo", seqA))
      client.nothingHappens(300.milliseconds)
      secondaryB.reply(SnapshotAck("foo", seqB))
      client.waitAck(setId)
    }

  }

}