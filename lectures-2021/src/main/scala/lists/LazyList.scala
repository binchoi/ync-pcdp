package lists

import java.util.concurrent.locks.ReentrantLock

class LazyList[T] extends ConcurrentSet[T] {

  // Add sentinels to start and end
  private val head: Node = new Node(Integer.MIN_VALUE)
  head.next = new Node(Integer.MAX_VALUE)

  /**
    * Check that prev and curr are still in list and adjacent
    */
  private def validate(pred: Node, curr: Node): Boolean =
    !pred.marked && !curr.marked && (pred.next eq curr)

  /**
    * Add an element.
    *
    * @param item element to add
    * @return true iff element was not there already
    */
  def add(item: T): Boolean = {
    val key = item.hashCode
    while (true) {
      var pred = this.head
      var curr = head.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      pred.lock()
      try {
        curr.lock()
        try {
          if (validate(pred, curr)) {
            if (curr.key == key) { // present
              return false
            }
            else { // not present
              val Node = new Node(item)
              Node.next = curr
              pred.next = Node
              return true
            }
          }
        } finally {
          curr.unlock()
        }
      } finally pred.unlock()
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
    val key = item.hashCode
    while (true) {
      var pred = this.head
      var curr = head.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      pred.lock()
      try {
        curr.lock()
        try {
          if (validate(pred, curr)) {
            if (curr.key != key) return false
            else { // absent
              curr.marked = true // logically remove
              pred.next = curr.next // physically remove
              return true
            }
          }
        } finally {
          curr.unlock()
        }
      } finally {
        // always unlock pred
        pred.unlock()
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
  def contains(item: T) = {
    val key = item.hashCode
    var curr = this.head
    while (curr.key < key) curr = curr.next
    val c = curr.key == key && !curr.marked
    if (!c) {
      checkFailCounter.getAndIncrement()
    }
    c
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

    /**
      * If true, Node is logically deleted.
      */
    var marked: Boolean = false

    /**
      * Constructor for sentinel Node
      *
      * @param key should be min or max int value
      */
    def this(key: Int) {
      this(null.asInstanceOf[T])
      this._key = Some(key)
    }

    def lock(): Unit = myLock.lock()

    def unlock(): Unit = myLock.unlock()

  }

}