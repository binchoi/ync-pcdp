package keyvaluestore

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import scala.concurrent.duration.FiniteDuration

object Tools {
  class TestRefWrappingActor(val probe: TestProbe) extends Actor {
    def receive: Receive = { case msg => probe.ref forward msg }
  }
}

/**
 * This is a utility to mix into your tests which provides convenient access 
 * to a given replica. It will keep track of requested updates and allow 
 * simple verification. See e.g. Step 1 for how it can be used.
 */
trait Tools { this: TestKit with ImplicitSender =>
  
  import Tools._

  def probeProps(probe: TestProbe): Props = Props(classOf[TestRefWrappingActor], probe)
  
  class Session(val probe: TestProbe, val replica: ActorRef) {
    import Replica._

    @volatile private var seq = 0L
    private def nextSeq: Long = {
      val next = seq
      seq += 1
      next
    }

    @volatile private var referenceMap = Map.empty[String, String]

    def waitAck(s: Long): Unit = probe.expectMsg(OperationAck(s))

    def waitFailed(s: Long): Unit = probe.expectMsg(OperationFailure(s))

    def set(key: String, value: String): Long = {
      referenceMap += key -> value
      val s = nextSeq
      probe.send(replica, Insert(key, value, s))
      s
    }

    def setAcked(key: String, value: String): Unit = waitAck(set(key, value))

    def remove(key: String): Long = {
      referenceMap -= key
      val s = nextSeq
      probe.send(replica, Remove(key, s))
      s
    }

    def removeAcked(key: String): Unit = waitAck(remove(key))

    def getAndVerify(key: String): Unit = {
      val s = nextSeq
      probe.send(replica, Get(key, s))
      probe.expectMsg(GetResult(key, referenceMap.get(key), s))
    }

    def get(key: String): Option[String] = {
      val s = nextSeq
      probe.send(replica, Get(key, s))
      probe.expectMsgType[GetResult].valueOption
    }

    def nothingHappens(duration: FiniteDuration): Unit = probe.expectNoMsg(duration)
  }

  def session(replica: ActorRef)(implicit system: ActorSystem) = new Session(TestProbe(), replica)


}