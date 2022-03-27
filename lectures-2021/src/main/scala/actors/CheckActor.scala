package actors

import akka.actor.{Actor, Props}

/**
  * Example 10: Identifying actors by their paths in the system
  */
class CheckActor extends Actor {

  import akka.actor.{ActorIdentity, Identify}

  def receive = {
    case path: String =>
      println(s"checking path $path")
      // 1. Send a message to all actors in the selection
      // This is a special message that requires to identify all actors
      context.actorSelection(path) ! Identify(path)

    // 2. Got a response from actorSelection with some reference
    case ActorIdentity(path, Some(ref)) =>
      println(s"found actor $ref on $path")

    // 3. Got a response from selection -- no reference found.
    case ActorIdentity(path, None) =>
      println(s"could not find an actor on $path")
  }
}


object ActorsIdentify extends App {
  val checker = ourSystem.actorOf(Props[CheckActor], "checker")
  
  // Referring to actors similarly to files in a file system

  checker ! "../*"
  Thread.sleep(1000)
  println()

//  checker ! "../../*"
//  Thread.sleep(1000)
//  println()

//  checker ! "/system/*"
//  Thread.sleep(1000)
//  println()

//  val checker3 = ourSystem.actorOf(Props[CheckActor], "checker3")
//  checker ! "/user/checker*"
//  Thread.sleep(1000)
//  println()

//  checker ! "/user/checker2"
//  Thread.sleep(1000)
//  println()

  ourSystem.stop(checker)
  Thread.sleep(1000)
  println()

  ourSystem.terminate()
}