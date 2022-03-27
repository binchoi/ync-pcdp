package actors

/**
  * Example 12: Ask-pattern and the use of futures
  */
import akka.actor._
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * An actor that simply responds to "ping" and stops itself
  */
class Pongy extends Actor {
  val log = Logging(context.system, this)
  
  def receive = {
    case "ping" =>
      
      // TODO: Uncomment later to demonstrate non-blocking piping
      Thread.sleep(1000)

      log.info("Got a ping -- ponging back!")
      sender ! "pong"
      context.stop(self)
  }
  
  override def postStop() = log.info("pongy going down")
}

/**
  * A class that sends ping, waits for the result and then terminates 
  */
class Pingy extends Actor {
  val log = Logging(context.system, this)
  
  def receive = {
    case pongyRef: ActorRef =>
      // What happens if we decrease the time-out?
      log.info("Sending ping")
      implicit val timeout = Timeout(2 seconds)
      // Sending ping an waiting for the future
      val future = pongyRef ? "ping"
      
      log.info("Forwarding response from Pongy to Masta")
      // Sends the value from the future to the sender
      // This is a non-blocking operation
      future.pipeTo(sender)
      log.info("Pingy is done with this stuff")
  }
}

/**
  * A master class orchestrating the ping-pong interaction
  */
class Master extends Actor {
  val log = Logging(context.system, this)
  
  val pingy = ourSystem.actorOf(Props[Pingy], "pingy")
  val pongy = ourSystem.actorOf(Props[Pongy], "pongy")
  
  def receive = {
    case "start" =>
      pingy ! pongy
    case "pong" =>
      log.info("got a pong back!")
      context.stop(self)
  }
  override def postStop() = log.info("master going down")

}

/**
  * Testing the ping-pong example
  */
object CommunicatingAsk extends App {
  val masta = ourSystem.actorOf(Props[Master], "masta")
  
  masta ! "start"
  Thread.sleep(5000)
  
  ourSystem.terminate()
}