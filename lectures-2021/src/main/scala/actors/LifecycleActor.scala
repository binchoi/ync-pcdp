package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

/**
  * Example 11: Tracking actor life cycle
  */


////////////////////////////////////////////
// A simple actor that prints its message
////////////////////////////////////////////
class StringPrinter extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case msg => log.info(s"child got message '$msg' from ${context.sender()}")
  }
  override def preStart(): Unit = log.info(s"child about to start.")
  override def postStop(): Unit = log.info(s"child just stopped.")
}

////////////////////////////////////////////
// A parent actor with lifecycle management
////////////////////////////////////////////
class LifecycleActor extends Actor {
  val log = Logging(context.system, this)

  var child: ActorRef = _
  
  // Message receiving is asynchronous
  def receive = {
    case num: Double  => log.info(s"got a double - $num")
    case num: Int     => log.info(s"got an integer - $num")
    case lst: List[_] => log.info(s"list - ${lst.head}, ...")
    case txt: String  => child ! txt
  }

  override def preStart(): Unit = {
    log.info("about to start")

    // Initially create a child actor
    // This is an asynchronous operation! - see the log 
    child = context.actorOf(Props[StringPrinter], "kiddo")
  }

  override def preRestart(reason: Throwable, msg: Option[Any]): Unit = {
    log.info(s"about to restart because of $reason, during message $msg")
    super.preRestart(reason, msg)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"just restarted due to $reason")
    super.postRestart(reason)
  }

  override def postStop() = log.info("just stopped")
}


object ActorsLifecycle extends App {
  val testy = ourSystem.actorOf(Props[LifecycleActor], "testy")
  
  // Message sending is non-blocking: fire and forget! 

  testy ! math.Pi
  Thread.sleep(1000)
  println()

  testy ! 7
  Thread.sleep(1000)
  println()

  testy ! "hi there!"
  Thread.sleep(1000)
  println()

  
  // TODO: Let send some junk now.
  
  testy ! Nil
  Thread.sleep(1000)
  println()

  testy ! "sorry about that"
  Thread.sleep(1000)
  println()

  ourSystem.stop(testy)
  Thread.sleep(1000)
  println()

  ourSystem.terminate()
}
