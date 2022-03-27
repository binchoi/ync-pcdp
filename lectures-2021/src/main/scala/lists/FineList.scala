package lists

import java.util.concurrent.locks.ReentrantLock

class FineList[T] extends ConcurrentSet[T] {

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
    val key = item.hashCode
    head.lock()
    var pred: Node = head
    try {
      var curr = pred.next
      curr.lock()
      try {
        while (curr.key < key) {
          pred.unlock()
          pred = curr
          curr = curr.next
          curr.lock()
        }
        if (curr.key == key) {
          return false
        }
        val newNode = new Node(item)
        newNode.next = curr
        pred.next = newNode
        true
      } finally {
        curr.unlock()
      }
    } finally {
      pred.unlock()
    }
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
    head.lock()
    try {
      pred = head
      curr = pred.next
      curr.lock()
      try {
        while (curr.key < key) {
          pred.unlock()
          pred = curr
          curr = curr.next
          curr.lock()
        }
        if (curr.key == key) {
          pred.next = curr.next
          return true
        }
        false
      } finally {
        curr.unlock()
      }
    } finally {
      pred.unlock()
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
    head.lock()
    try {
      pred = head
      curr = pred.next
      curr.lock()
      try {
        while (curr.key < key) {
          pred.unlock()
          pred = curr
          curr = curr.next
          curr.lock()
        }
        val c = curr.key == key
        if (!c) {
          checkFailCounter.getAndIncrement()
        }
        c
      } finally {
        curr.unlock()
      }
    } finally {
      pred.unlock()
    }
  }

  /**
    * list Node
    */
  private class Node(val item: T) {
    /**
      * synchronizes individual Node
      */
    private val myLock = new ReentrantLock()

    private var _key : Option[Int] = None
    /**
      * item's hash code
      */
    def key = if (_key.isEmpty) item.hashCode() else _key.get
    /**
      * next Node in list
      */
    @volatile
    var next: Node = _

    def lock(): Unit = myLock.lock()

    def unlock(): Unit = myLock.unlock()

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
