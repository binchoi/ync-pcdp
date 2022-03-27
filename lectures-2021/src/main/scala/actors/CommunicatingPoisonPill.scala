package actors

import akka.actor.Props

/**
  * Example 14: Sending a poison pill to the actor
  */
object CommunicatingPoisonPill extends App {
  val masta = ourSystem.actorOf(Props[Master], "masta")

  masta ! "start"
  masta ! akka.actor.PoisonPill

  Thread.sleep(1000)
  ourSystem.terminate()
}
