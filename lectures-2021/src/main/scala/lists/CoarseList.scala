package lists

import java.util.concurrent.locks.ReentrantLock

class CoarseList[T] extends ConcurrentSet[T] {

  // Add sentinels to start and end
  private val head: Node = new Node(Integer.MIN_VALUE)
  private val tail: Node = new Node(Integer.MAX_VALUE)
  head.next = tail

  /**
    * Synchronizes access to list
    */
  private val lock = new ReentrantLock

  /**
    * Add an element.
    *
    * @param item element to add
    * @return true iff element was not there already
    */
  def add(item: T): Boolean = {
    var pred: Node = null
    var curr: Node = null
    val key = item.hashCode
    lock.lock()
    try {
      pred = head
      curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      if (key == curr.key) false
      else {
        val node = new Node(item)
        node.next = curr
        pred.next = node
        true
      }
    } finally lock.unlock()
  }

  /**
    * Remove an element.
    *
    * @param item element to remove
    * @return true iff element was present
    */
  def remove(item: T): Boolean = {
    var pred: Node = null
    var curr: Node = null
    val key = item.hashCode
    lock.lock()
    try {
      pred = this.head
      curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      if (key == curr.key) { // present
        pred.next = curr.next
        true
      }
      else false // not present
    } finally {
      // always unlock
      lock.unlock()
    }
  }

  /**
    * Test whether element is present
    *
    * @param item element to test
    * @return true iff element is present
    */
  def contains(item: T): Boolean = {
    var pred: Node = null
    var curr: Node = null
    val key = item.hashCode
    lock.lock()
    try {
      pred = head
      curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      val c = key == curr.key
      if (!c) {
        checkFailCounter.getAndIncrement()
      }
      c
    } finally lock.unlock()
  }

  /**
    * list Node
    */
  private class Node(val item: T) {
    
    private var _key : Option[Int] = None
    /**
      * item's hash code
      */
    def key = if (_key.isEmpty) item.hashCode() else _key.get
    /**
      * next Node in list
      */
    var next: Node = _

    /**
      * Constructor for sentinel Node
      *
      * @param key should be min or max int value
      */
    def this(key: Int) {
      this(null.asInstanceOf[T])
      this._key = Some(key)
    }
  }

}
