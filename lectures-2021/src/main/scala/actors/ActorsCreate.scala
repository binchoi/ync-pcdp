package actors

import akka.actor.ActorRef

/**
  * Example 02: creating a simple actor and interacting with it.
  */
object ActorsCreate  {

  def main(args: Array[String]): Unit = {

    // Create an actor instance and get a reference to it
    val hiActor: ActorRef = ourSystem.actorOf(HelloActor.props("hi"), name = "greeter")

    // Send a message to the actor
    hiActor ! "hi"
    
    // Wait a bit
//    Thread.sleep(1000)
    
    // Send another message
    hiActor ! "hola"
    hiActor ! "hola1"
    
    // Wait more
//    Thread.sleep(1000)
    
    // Shut down the actor system
    ourSystem.terminate()
  }

}