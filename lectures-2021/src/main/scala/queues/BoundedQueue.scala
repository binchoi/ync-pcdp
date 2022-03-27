package queues

import concurrent.ConcurrentQueue

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock


/**
  * Bounded blocking queue
  *
  * @tparam T item type
  * @author Maurice Herlihy, Ilya Sergey
  */
class BoundedQueue[T](private val capacity: Int) extends ConcurrentQueue[T] {

  /**
    * Lock out other enqueuers (dequeuers)
    */
  private val deqLock = new ReentrantLock()
  private val enqLock = new ReentrantLock()
  /**
    * wait/signal when queue is not empty or not full
    */
  private val notFullCondition = enqLock.newCondition()
  private val notEmptyCondition = deqLock.newCondition()

  /**
    * Number of empty slots.
    */
  private val remaining = new AtomicInteger(capacity)
  /**
    * First entry in queue.
    *
    */
  private var head: Node = new Node(null.asInstanceOf[T])
  /**
    * Last entry in queue.
    */
  private var tail: Node = head

  /**
    * Remove and return head of queue.
    */
  def deq(): T = {
    var result: T = null.asInstanceOf[T]
    var mustWakeEnqueuers = true
    deqLock.lock()
    try {
      while (remaining.get == capacity) try
        notEmptyCondition.await()
      catch {
        case ex: InterruptedException =>
      }
      result = head.next.value
      head = head.next
      if (remaining.getAndIncrement == 0) {
        mustWakeEnqueuers = true
      }
    } finally {
      deqLock.unlock()
    }
    if (mustWakeEnqueuers) {
      enqLock.lock()
      try {
        notFullCondition.signalAll()
      } finally {
        enqLock.unlock()
      }
    }
    result
  }

  /**
    * Append item to end of queue.
    */
  def enq(x: T): Unit = {
    if (x == null) throw new NullPointerException
    var mustWakeDequeuers = false
    enqLock.lock()
    try {
      while (remaining.get == 0) try {
        notFullCondition.await()
      } catch {
        case e: InterruptedException =>
          println(s"Enqueueing of $x is interrupted")
      }
      val e = new Node(x)
      tail.next = e
      tail = e
      // TODO: Notice how we are decrementing here!
      if (remaining.getAndDecrement == capacity) {
        mustWakeDequeuers = true
      }
    } finally {
      enqLock.unlock()
    }
    if (mustWakeDequeuers) {
      deqLock.lock()
      try {
        notEmptyCondition.signalAll()
      } finally {
        deqLock.unlock()
      }
    }
  }

  /**
    * Individual queue item.
    */
  protected class Node(val value: T) {
    var next: Node = null.asInstanceOf[Node]
  }

  override def toListThreadSafe: List[T] = {
    deqLock.lock()
    enqLock.lock()
    try {
      var result: List[T] = Nil
      var node = head.next
      while (node != null) {
        result = node.value :: result
        node = node.next
      }
      result.reverse
    } finally {
      deqLock.unlock()
      enqLock.unlock()

    }

  }
}
