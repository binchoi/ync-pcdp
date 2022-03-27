package actors

import akka.actor.{Actor, Props}

/**
  * Example 05: an actor as a state-transition system
  */
class CountdownActor extends Actor {

  var n = 10

  // State "counting"
  def counting: Actor.Receive = {
    case "count" =>
      if (n == 1) context.become(done)
      n -= 1
      println(s"n = $n")
  }

  // State "done"
  def done: Receive = PartialFunction.empty

  // Initial state
  def receive: Receive = counting

}


object ActorsCountdown extends App {
  
  val countdown = ourSystem.actorOf(Props[CountdownActor])
  
  for (i <- 0 until 20) countdown ! "count"

  // Testing: the actor will only count from 10 to 0
  Thread.sleep(1000)
  
  ourSystem.terminate()
}