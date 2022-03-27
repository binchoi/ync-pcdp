package actors

import akka.actor._
import akka.event.Logging

/**
  * Example 17: Working with remote actors
  */


/**
  * An application that will be running the pongy actor for 20 seconds.
  * Start it first.
  */
object RemotingPongySystem extends App {
  // Picking an arbitrary port
  // If fails, pick anything in the range 1024-65535
  val system = remotingSystem("PongyDimension", 24321)
  val pongy = system.actorOf(Props[Pongy], "pongy")
  Thread.sleep(30000)
  system.terminate()
}

/**
  * A mediator class that will run in Pingy dimension
  */
class Runner extends Actor {
  val log = Logging(context.system, this)
  val pingy = context.actorOf(Props[Pingy], "pingy")

  def receive = {
    case "start" =>
      // Ask pongy for it reference
      val path = context.actorSelection("akka.tcp://PongyDimension@127.0.0.1:24321/user/pongy")
      path ! Identify(0)

    case ActorIdentity(0, Some(ref)) =>
      // Good! Got the reference for pongy, send it to our local pingy
      pingy ! ref

    // This should not happen  
    case ActorIdentity(0, None) =>
      // Something went wrong
      log.info("Something's wrong -- no pongy anywhere!")
      context.stop(self)
      
    case "pong" =>
      // Got a pong from a distributed actor - cool!!
      log.info("got a pong from another dimension. It works!")
      context.stop(self)
  }
}


/**
  * An application that will be communicating with pongy via pingy.
  * Start it second.
  */
object RemotingPingySystem extends App {
  
  // Start our system on this port
  val system = remotingSystem("PingyDimension", 24567)
  
  // Start an actor in our system
  val runner = system.actorOf(Props[Runner], "runner")
  
  // Start the whole thing
  runner ! "start"
  Thread.sleep(5000)
  
  system.terminate()
}

