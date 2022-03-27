package queues

import concurrent.{ConcurrentQueue, EmptyException}

import java.util.concurrent.locks.ReentrantLock

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class UnboundedQueue[T] extends ConcurrentQueue[T] {

  /**
    * Lock out other enqueuers (dequeuers)
    */
  private val enqLock = new ReentrantLock()
  private val deqLock = new ReentrantLock()

  /**
    * First entry in queue.
    * 
    * TODO: Explain why do we need a sentinel
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
    deqLock.lock()
    try {
      if (head.next == null) {
        throw EmptyException
      }
      // Take node after the sentinel
      result = head.next.value
      
      head = head.next
      
      // TODO: Qeustion: why it's okay to use two different locks
      //       for enqueueing and dequeueing?
      //       I.e., why there is no lost update?
    } finally {
      deqLock.unlock()
    }
    result
  }

  /**
    * Append item to end of queue.
    */
  def enq(x: T): Unit = {
    val e = new Node(x)
    enqLock.lock()
    try {
      // TODO: [Q] What happens if dequeueing goes on at the same time? 
      tail.next = e
      tail = e
    } finally {
      enqLock.unlock()
    }
  }

  /**
    * Individual queue item.
    */
  protected class Node(val value: T) {
    var next: Node = null.asInstanceOf[Node]
  }

  override def toListThreadSafe: List[T] = {
    enqLock.lock()
    deqLock.lock()
    try {
      var result: List[T] = Nil
      var node = head.next
      while (node != null) {
        result = node.value :: result
        node = node.next
      }
      result.reverse
    } finally {
      enqLock.unlock()
      deqLock.unlock()
    }

  }


}
