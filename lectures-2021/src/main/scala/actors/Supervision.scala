package actors

import akka.actor.SupervisorStrategy._
import akka.actor._
import akka.event.Logging


/**
  * Example 16: Actor supervision
  */


/**
  * A custom exception class
  */
case object HelloException extends Exception("Hello Exception")

/**
  * A bad-behaving actor
  */
class Naughty extends Actor {
  val log = Logging(context.system, this)

  var state = 0

  def receive: Receive = {
    case "hello" => {
      state += 10
      throw HelloException
    }
    case s: String => {
      log.info(s + s" [current state: $state]")
      state += 10
    }
    case msg => {
      throw new RuntimeException
    }
  }

  override def postRestart(t: Throwable) = {
    log.info("naughty restarted")
  }
}

/**
  * A supervising actor
  */
class Supervisor extends Actor {
  val child = context.actorOf(Props[Naughty], "victim")

  def receive = PartialFunction.empty

  override val supervisorStrategy =
  // Do for so for the actor who has thrown an exception
    OneForOneStrategy() {
      case HelloException => Resume // Resume the actor execution
      case ake: ActorKilledException => Restart // Create a new actor of that kind
      case _ => Escalate // Propagate the exception
    }
}


object SupervisionKill extends App {
  val s = ourSystem.actorOf(Props[Supervisor], "super")

  ourSystem.actorSelection("/user/super/*") ! "hello"

  ourSystem.actorSelection("/user/super/*") ! "are you alright?"

  // TODO: Kill is weaker than PoisonPill -- it doesn't terminate the actor permanently
  ourSystem.actorSelection("/user/super/*") ! Kill

  ourSystem.actorSelection("/user/super/*") ! "sorry about that"

  ourSystem.actorSelection("/user/super/*") ! "kaboom".toList

  Thread.sleep(1000)
  ourSystem.stop(s)

  Thread.sleep(1000)
  ourSystem.terminate()
}