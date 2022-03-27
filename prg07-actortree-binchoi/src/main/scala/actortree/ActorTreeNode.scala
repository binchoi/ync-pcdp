package actortree

import akka.actor.{Actor, ActorRef, Props}

import java.util.concurrent.atomic.AtomicInteger
//import javax.swing.text.Position

object ActorTreeNode {

  trait Position

  case object Left extends Position

  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean): Props = Props(classOf[ActorTreeNode], elem, initiallyRemoved)
}

class ActorTreeNode(val elem: Int, var initiallyRemoved: Boolean) extends Actor {

  import ActorTreeNode._
  import ActorTreeSet._

  var subtrees: Map[Position, ActorRef] = Map[Position, ActorRef]()
  var removed: Boolean = initiallyRemoved
  var expected_copyToResponses: Int = -1
  var id_counter: AtomicInteger = new AtomicInteger(0)

  /**
    * Delegate the operation to a child node
    */
  private def delegateToChild(pos: Position, op: Operation): Unit = subtrees(pos) ! op

  def receive: Receive = normal

  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive = {
    case Insert(requester: ActorRef, id: Int, new_elem: Int) =>
      if (new_elem==elem) {
        removed = false
        requester ! OperationFinished(id)
      } else {
        val next_pos = if (new_elem < elem) Left else Right
        if (subtrees.contains(next_pos)) delegateToChild(next_pos, Insert(requester, id, new_elem))
        else {
          subtrees += (next_pos -> context.actorOf(props(new_elem, initiallyRemoved = false)))
          requester ! OperationFinished(id)
        }
      }

    case Contains(requester: ActorRef, id: Int, some_elem: Int) =>
      if (some_elem==elem) requester ! ContainsResult(id, result = !removed)
      else {
        val next_pos = if (some_elem < elem) Left else Right
        if (subtrees.contains(next_pos)) delegateToChild(next_pos, Contains(requester, id, some_elem))
        else requester ! ContainsResult(id, result = false)
      }

    case Remove(requester: ActorRef, id: Int, some_elem: Int) =>
      if (some_elem==elem) {
        removed = true
        requester ! OperationFinished(id)
      } else {
        val next_pos = if (some_elem < elem) Left else Right
        if (subtrees.contains(next_pos)) delegateToChild(next_pos, Remove(requester, id, some_elem))
        else requester ! OperationFinished(id)
      }

    case CopyTo(treeNode: ActorRef) =>
      val children_nodes = subtrees.values
      if (removed) {
        if (children_nodes.isEmpty) context.parent ! CopyFinished
        else {
          children_nodes.foreach(_ ! CopyTo(treeNode))
          expected_copyToResponses = children_nodes.size
          context.become(copying, discardOld = false)
        }
      } else {
        treeNode ! Insert(self, id_counter.getAndIncrement(), elem) // insert current node
        children_nodes.foreach(_ ! CopyTo(treeNode))
        expected_copyToResponses = children_nodes.size + 1 // including current node
        context.become(copying, discardOld = false)
      }
  }

  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
  def copying: Receive = {
      case CopyFinished =>
        expected_copyToResponses -= 1
        if (expected_copyToResponses==0) {
          context.parent ! CopyFinished
          context.unbecome()
        }

      case OperationFinished(_) =>
        expected_copyToResponses -= 1
        if (expected_copyToResponses==0) {
          context.parent ! CopyFinished
          context.unbecome()
        }
    }

}
