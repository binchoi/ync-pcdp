package lists

import java.util.concurrent.locks.ReentrantLock

class OptimisticList[T] extends ConcurrentSet[T] {

  // Add sentinels to start and end
  private val head: Node = new Node(Integer.MIN_VALUE)
  head.next = new Node(Integer.MAX_VALUE) // tail

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
    val key = item.hashCode()
    while (true) {
      var pred = this.head
      var curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      pred.lock()
      curr.lock()
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present
            return false
          } else { // not present
            val entry = new Node(item)
            entry.next = curr
            pred.next = entry
            return true
          }
        }
      } finally { // always unlock
        pred.unlock()
        curr.unlock()
      }
    }
    false
  }

  /**
    * Remove an element.
    *
    * @param item element to remove
    * @return true iff element was present
    */
  def remove(item: T): Boolean = {
    val key = item.hashCode()
    while (true) {
      var pred = this.head
      var curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      pred.lock()
      curr.lock()
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present in list
            pred.next = curr.next
            return true
          } else { // not present in list
            return false
          }
        }
      } finally { // always unlock
        pred.unlock()
        curr.unlock()
      }
    }
    false
  }

  /**
    * Test whether element is present
    *
    * @param item element to test
    * @return true iff element is present
    */
  def contains(item: T): Boolean = {
    val key = item.hashCode()
    while (true) {
      var pred = this.head
      // sentinel node;
      var curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      try {
        pred.lock()
        curr.lock()
        if (validate(pred, curr)) {
          return curr.key == key
        } else {
          checkFailCounter.getAndIncrement()
        }
      } finally { // always unlock
        pred.unlock()
        curr.unlock()
      }
    }
    false
  }

  private def validate(pred: Node, curr: Node): Boolean = {
    var entry = head
    while (entry.key <= pred.key) {
      if (entry eq pred) {
        // Checking for reference equality
        return pred.next eq curr
      }
      entry = entry.next
    }
    false
  }

  /**
    * list Node
    */
  private class Node(val item: T) {
    /**
      * synchronizes individual Node
      */
    private val myLock = new ReentrantLock()

    private var _key: Option[Int] = None

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

