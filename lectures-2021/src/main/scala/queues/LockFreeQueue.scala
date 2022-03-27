package queues

import concurrent.{ConcurrentQueue, EmptyException}

import java.util.concurrent.atomic.AtomicReference

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class LockFreeQueue[T] extends ConcurrentQueue[T] {

  /**
    * First entry in queue.
    */

  private val sentinel = new Node(null.asInstanceOf[T])
  private val head = new AtomicReference[Node](sentinel)
  /**
    * Last entry in queue.
    */
  private val tail = new AtomicReference[Node](sentinel)

  /**
    * Remove and return head of queue.
    */
  def deq(): T = {
    var spin = true
    var result = null.asInstanceOf[T]
    while (spin) {
      val first = head.get()
      val last = tail.get()
      val next = first.next.get()
      // No changes since read the first
      if (first == head.get()) {
        // The queue seems empty
        if (first == last) {
          // is it really empty?
          if (next == null) {
            // Okay, report an empty queue
            throw EmptyException
          }
          // No, it's not empty: someone advanced the tail
          // Let us help by installing the head's next to the tail
          // No worries if we failed
          tail.compareAndSet(last, next)
        } else {
          // Okay, the queue seems non-empty 
          result = next.value
          // When we were reading the result, was it still in the haed
          if (head.compareAndSet(first, next)) {
            // Yeah, it was, stop looping
            spin = false
          }
          // Let's try again
        }
      }
    }
    result
  }

  /**
    * Append item to end of queue.
    */
  def enq(x: T): Unit = {
    val node = new Node(x)
    while (true) {
      // Check the tail
      val last = tail.get()
      // Check the tail's next
      val next = last.next.get()
      // Are we still at the same tail
      if (last == tail.get) {
        // Does the tail still have no next element
        if (next == null) {
          // Okay, let's try to install first the tail's successor
          if (last.next.compareAndSet(next, node)) {
            // Done, now let's try to update the tail itself
            tail.compareAndSet(last, node)
            // No worries if failed: someone helped us
            return 
          }
          // Failed, let's try from scratch
        } else {
          // The tail now has the next element.
          // That means that someone has installed it in the meantime.
          // Shall we help to update the tail and then try again
          // with our enqueueing?
          tail.compareAndSet(last, next)
        }
      }
    }
  }

  /**
    * Individual queue item.
    */
  protected class Node(val value: T) {
    val next: AtomicReference[Node] =
      new AtomicReference[Node](null.asInstanceOf[Node])
  }

  override def toListThreadSafe: List[T] = toListUnsafe


}
