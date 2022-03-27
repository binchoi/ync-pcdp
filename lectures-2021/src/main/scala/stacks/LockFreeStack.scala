package stacks

import concurrent.EmptyException
import spinlocks.Backoff

import java.util.concurrent.atomic.AtomicReference

/**
  * @author Ilya Sergey
  */
class LockFreeStack[T] extends ConcurrentStack[T] {

  val top = new AtomicReference[Node](null.asInstanceOf[Node])
  val MIN_DELAY = 128
  val MAX_DELAY = 4096
  val backoff = new Backoff(MIN_DELAY, MAX_DELAY)

  protected def tryPush(node: Node): Boolean = {
    val oldTop = top.get()
    node.next = oldTop
    top.compareAndSet(oldTop, node)
  }

  override def push(value: T): Unit = {
    val node = new Node(value)
    while (true) {
      if (tryPush(node)) {
        return
      } else {
        backoff.backoff()
      }
    }
  }

  protected def tryPop(): Node = {
    val oldTop = top.get()
    if (oldTop == null) {
      throw EmptyException
    }
    val newTop = oldTop.next
    if (top.compareAndSet(oldTop, newTop)) {
      oldTop
    } else {
      null
    }
  }

  override def pop(): T = {
    while (true) {
      val returnNode = tryPop()
      if (returnNode != null) {
        return returnNode.value
      } else {
        backoff.backoff()
      }
    }
    throw new Exception("[LockFreeStack] Cannot happen")
  }

  class Node(val value: T,
             var next: Node = null.asInstanceOf[Node])

  override def toListThreadUnsafe: List[T] = {
    var curr = top.get()
    if (curr == null) {
      return Nil
    }
    var result: List[T] = Nil
    while (curr != null) {
      result = curr.value :: result
      curr = curr.next
    }
    result.reverse
  }
}
