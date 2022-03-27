package actors

import akka.actor.{Actor, Props}

/**
 * Example 09: Actor hierarchy
 */

///////////////////////////////////////////////////
// A child actor
///////////////////////////////////////////////////
class ChildActor extends Actor {

  def receive = {
    case "sayhi" =>
      val parent = context.parent
      println(s"my parent ${parent.path} made me say hi!")
  }

  override def postStop() {
    println(s"child stopped!")

    // println(s"${self.path.name} stopped!")
  }
}

///////////////////////////////////////////////////
// A parent actor
///////////////////////////////////////////////////
class ParentActor extends Actor {

  var n = 1

  def receive: Receive = {
    // Create a child actor
    case "create" =>
      context.actorOf(Props[ChildActor], "child" + n)
      n += 1
      println(s"created a new child\nchildren: ${context.children.map(_.path)}")

    // Print a message recursively
    case "sayhi" =>
      println("Kids, say hi!")
      for (c <- context.children) c ! "sayhi"

    // Stop itself an all the children
    case "stop" =>
      println("parent stopping")
      context.stop(self)
  }

  override def postStop() {
    println(s"parent stopped!")

    // println(s"${self.path.name} stopped!")
  }

}

///////////////////////////////////////////////////
// Experimenting with the actors
///////////////////////////////////////////////////
object ActorsHierarchy extends App {
  val parent = ourSystem.actorOf(Props[ParentActor], "parent")
  parent ! "create"
  parent ! "create"
  Thread.sleep(1000)

  parent ! "sayhi"
  // Notice that the actors are about to stop now

  // TODO: Uncomment me!
  parent ! "stop"
  Thread.sleep(1000)

  // TODO: Let's discuss asynchronous communication 
  //  and message delivery guarantees

  ourSystem.terminate()
}


