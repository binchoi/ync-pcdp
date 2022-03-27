package actors

import akka.actor.Props

/**
  * Talking to actors directly
  */
object TalkToChildren extends App {
  
  // TODO: Create a parent actor and two children (see ActorHierarchy)
  
  // Make children to say something without sending a messages to the parent 
  
  val parent = ourSystem.actorOf(Props[ParentActor], "parent")
  parent ! "create"
  parent ! "create"
  Thread.sleep(1000)

  ourSystem.actorSelection("/user/parent/*") ! "sayhi" 
  ourSystem.actorSelection("/user/parent/child2") ! "sayhi"
  
  // TODO: How can we send message to all children?

  Thread.sleep(1000)

  ourSystem.terminate()
}