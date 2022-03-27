package actors

import actors.DictionaryActor.{End, Init, IsWord}
import akka.actor.Actor
import akka.event.Logging

import scala.collection.mutable
import scala.io.Source

/**
  * Example 08: a state-machine actor implementing a dictionary
  * 
  * This is an exercise.
  */
class DictionaryActor extends Actor {
  
  private val log = Logging(context.system, this)
  private val dictionary = mutable.Set[String]()
 
  def receive: PartialFunction[Any, Unit] = uninitialized
  
  def uninitialized: PartialFunction[Any, Unit] = {
    case Init(path) =>
      // Read the file contents from the path
      val f = Source.fromFile(path)
      // Use f.getLines to go through the file contents
      f.getLines().foreach(dictionary.add)
      context.become(initialized, discardOld = false)
      f.close()   
    case _ => {
      sender() ! "uninitialized"
    }
  }
  
  def initialized: PartialFunction[Any, Unit] = {
    case IsWord(w) =>
      val result = dictionary.contains(w)
      sender() ! result
      println(s"Is this a word $w? ${if (result) "Yes" else "No"}")
    case End =>
      // Clear the dictionary
      dictionary.clear()
      context.unbecome()
  }
  
  override def unhandled(msg: Any) = {
    log.info(s"message $msg should not be sent in this state.")
  }
}


object DictionaryActor {
  case class Init(path: String)
  case class IsWord(w: String)
  case object End
}