package actors

import akka.actor.{Actor, Props}
import akka.event.Logging

/**
  * Example 03: demonstration of processing unhandled messages 
  */
class DeafActor extends Actor {
  val log = Logging(context.system, this)

  // This actor doesn't know how to process any messages
  def receive: PartialFunction[Any, Unit] = PartialFunction.empty

  // Overriding the standard behaviour
  override def unhandled(msg: Any): Unit = msg match {
    case msg: String => log.info(s"could not handle '$msg'")
    // Ignore forever
    case _ => super.unhandled(msg)
  }
}

/**
  * Demonstration of the deaf actor
  */
object ActorsUnhandled extends App {

  val deafActor = ourSystem.actorOf(Props[DeafActor], name = "deafy")

  deafActor ! "hi"
  // Will be printing using `unhandled` behaviour
  Thread.sleep(1000)

  deafActor ! 1234

  Thread.sleep(1000)

  ourSystem.terminate()
}

// TODO: Can we move the String case to the `receive` function of the DeafActor?

// TODO: Why does it make sense to keep it separate?