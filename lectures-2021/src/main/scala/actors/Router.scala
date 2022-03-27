package actors

import akka.actor.{Actor, Props}

/**
  * Example 13: Router actor and message forwarding
  */

class Router extends Actor {
  var i = 0
  val children = for (_ <- 0 until 4) yield context.actorOf(Props[StringPrinter])
  
  def receive: Receive = {
    case "stop" => context.stop(self)
    case msg =>
      children(i) forward msg
      i = (i + 1) % 4
  }
}


object CommunicatingRouter extends App {
  val router = ourSystem.actorOf(Props[Router], "router")
  
  // Forwarding to two different printers
  router ! "Hi."
  router ! "I'm talking to you!"
  // Check the id of the receivers -- they should be different!
  
  Thread.sleep(1000)
  
  router ! "stop"
  Thread.sleep(1000)
  
  ourSystem.terminate()
}