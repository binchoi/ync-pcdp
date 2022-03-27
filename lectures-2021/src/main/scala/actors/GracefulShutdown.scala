package actors

import akka.actor._
import akka.event.Logging
import akka.pattern

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util._

/**
  * Example 15: DeathWatch and Graceful stop
  */
class GracefulPingy extends Actor {

  val log = Logging(context.system, this)

  // A child actor
  val pongy = context.actorOf(Props[Pongy], "pongy")

  // A death watch (monitoring)
  context.watch(pongy)

  def receive: Receive = {
    case "Shutdown" =>
      log.info("Pingy is shutting down Pongy")
      context.stop(pongy)
    case Terminated(`pongy`) =>
      log.info("Pingy stopping self")
      context.stop(self)
  }
}

object CommunicatingGracefulStop extends App {

  val grace = ourSystem.actorOf(Props[GracefulPingy], "grace")

  // Graceful stop implementation
  // Send the graceful stop with waiting for three minutes

  val stopped: Future[Boolean] = pattern.gracefulStop(grace, 3.seconds, "Shutdown")

  stopped onComplete {
    case Success(x) =>
      println("graceful shutdown successful")
      ourSystem.terminate()
    case Failure(t) =>
      println("grace not stopped!")
      ourSystem.terminate()
  }
}