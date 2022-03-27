package actors

import akka.actor.Actor

/**
  * Example 04: anti-pattern for behavioural change
  */
class BadCountDownActor extends Actor {
  var n = 10

  override def receive: PartialFunction[Any, Unit] =
    if (n > 0) { // Don't do this!
      // You don't want to reason about your actor's behaviour
      // at each moment of time by means of interpreting all these conditions!
      case "count" =>
        println(s"n = $n")
        n -= 1
    } else PartialFunction.empty

}
