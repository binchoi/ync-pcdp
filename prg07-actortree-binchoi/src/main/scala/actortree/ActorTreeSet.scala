package actortree

import akka.actor._
import scala.collection.immutable.Queue

object ActorTreeSet {

  trait Operation {
    def requester: ActorRef

    def id: Int

    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection */
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply

  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}


class ActorTreeSet extends Actor {

  import ActorTreeSet._
  import ActorTreeNode._

  private def makeRoot: ActorRef =
    context.actorOf(ActorTreeNode.props(0, initiallyRemoved = true))

  var root = makeRoot

  // optional
  var pendingQueue = Queue.empty[Operation]

  // optional
  def receive: Receive = normal

  // optional
  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = {
    case msg : Operation => root ! msg

    case GC =>
      val new_root = makeRoot
      root ! CopyTo(new_root)
      context.become(handleDuringGC(new_root), discardOld = false)

    case _ => sender() ! "I could not understand your message"
  }

  // optional
  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def handleDuringGC(newRoot: ActorRef): Receive = {
    case msg: Operation =>
      pendingQueue = pendingQueue.enqueue(msg)

    case CopyFinished =>
      while (pendingQueue.nonEmpty) {
        val (msg, remaining_queue) = pendingQueue.dequeue
        pendingQueue = remaining_queue
        newRoot ! msg
      }
      root ! PoisonPill
      root = newRoot
      context.unbecome()

    case GC =>

    case _ => sender() ! "I could not understand your message"
  }

}
