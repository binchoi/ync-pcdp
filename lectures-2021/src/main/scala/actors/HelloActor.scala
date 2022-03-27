package actors

import akka.actor.{Actor, Props}
import akka.event.Logging

/**
  * Example 01: a simple actor that logs received messages 
  */
class HelloActor(val hello: String) extends Actor {

  // An object for logging
  val log = Logging(context.system, this)

  // A method determining the reactions of an actor to various messages
  def receive: PartialFunction[Any, Unit] = {
    // Match against the message that is literally equal 
    // to the class parameter `hello`
    case `hello` =>
      // Same as println but with more information
      log.info(s"Received a '$hello'... $hello!")
    case msg =>
      log.info(s"Unexpected message '$msg'")
      // Stop running this actor instance
      //context.stop(self)
  }
}

/**
  * This is a "companion object" that shares the same names as a `HelloActor` class.   
  */
object HelloActor {

  //  Create an actor configuration.
  //  An instance of an object, determining how to create a new object.
  //  You can think of it as of a "factory" of objects
  //  Note the usage of the lazy argument passing
  def props(hello: String) = Props(new HelloActor(hello))
  
  // TODO: Emphasise the environment capturing.
  // Every value taken for actor creation will be packaged with it.
  // This might lead to memory leaks.

  // The same as above, but will create an actors from the class type and
  // given arguments.
  def propsAlt(hello: String) = Props(classOf[HelloActor], hello)
}