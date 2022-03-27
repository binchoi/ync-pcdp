package actors

import akka.actor.Actor
import akka.event.Logging

/**
  * Example 06: an actor that responds at the end
  */
class CountDownActorWithResponse extends Actor {
  
  val log = Logging(context.system, this)

  var n = 10

  // State "counting"
  def counting: Actor.Receive = {
    case "count" =>
      n -= 1
      // println(s"n = $n")
      if (n == 0) {
        // Send the message back to the sender
        sender() ! "Done counting"
        context.become(done)
      }
  }

  // State "done"
  def done: Receive = {
    case _ => sender() ! "Done counting"
  }

  // Initial state
  def receive: Receive = counting

}
