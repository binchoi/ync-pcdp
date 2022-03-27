package keyvaluestore

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class Step1_PrimarySpec extends TestKit(ActorSystem("Step1PrimarySpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll 
  with Tools {

  override def afterAll(): Unit = {
    system.terminate()
  }

  import Mediator._
  
  "[01-Primary]" must {

    "case 1: Primary (in isolation) should properly register itself to the provided mediator" in {
      val mediator = TestProbe()
      system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = true)), "case1-primary")

      mediator.expectMsg(Join)
    }

    "case 2: Primary (in isolation) should react properly to Insert, Remove, Get" in {
      val mediator = TestProbe()
      val primary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = true)), "case2-primary")
      val client = session(primary)

      mediator.expectMsg(Join)
      mediator.send(primary, JoinedPrimary)

      client.getAndVerify("k1")
      client.setAcked("k1", "v1")
      client.getAndVerify("k1")
      client.getAndVerify("k2")
      client.setAcked("k2", "v2")
      client.getAndVerify("k2")
      client.removeAcked("k1")
      client.getAndVerify("k1")
    }

//    "case 2 extended: Primary (in isolation) should react properly to Insert, Remove, Get" in {
//      val mediator = TestProbe()
//      val primary = system.actorOf(Replica.props(mediator.ref, Persistence.props(flaky = true)), "case2-ext-primary")
//      val client = session(primary)
//
//      mediator.expectMsg(Join)
//      mediator.send(primary, JoinedPrimary)
//
//      client.getAndVerify("k1")
//      client.setAcked("k1", "v1")
//      client.setAcked("k1", "v2")
//      client.setAcked("k1", "v2")
//      client.setAcked("k1", "v2")
//      client.setAcked("k1", "v5")
//      client.setAcked("k1", "v4")
//      client.setAcked("k1", "v3")
//      client.getAndVerify("k2")
//      client.getAndVerify("k1")
//      client.setAcked("k2", "v2")
//      client.getAndVerify("k2")
//      client.removeAcked("k1")
//      client.getAndVerify("k1")
//      client.removeAcked("k2")
//      client.getAndVerify("k2")
//    }
  }

  

}