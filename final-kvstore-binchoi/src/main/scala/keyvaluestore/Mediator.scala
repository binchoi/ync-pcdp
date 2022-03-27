package keyvaluestore

import akka.actor.{Actor, ActorRef}

object Mediator {
  case object Join

  case object JoinedPrimary
  case object JoinedSecondary

  /**
   * This message contains all replicas currently known to the mediator, including the primary.
   */
  case class Replicas(replicas: Set[ActorRef])
}

class Mediator extends Actor {
  import Mediator._
  var leader: Option[ActorRef] = None
  var replicas = Set.empty[ActorRef]

  def receive = {
    case Join =>
      if (leader.isEmpty) {
        leader = Some(sender)
        replicas += sender
        sender ! JoinedPrimary
      } else {
        replicas += sender
        sender ! JoinedSecondary
      }
      leader foreach (_ ! Replicas(replicas))
      // send the primary replica a Replicas msg containing a set
      // of actor refs of all replicas in the cluster including the primary replica
  }

}
