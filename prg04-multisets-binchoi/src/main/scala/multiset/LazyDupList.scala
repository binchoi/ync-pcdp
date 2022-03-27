package multiset

import java.util.concurrent.locks.ReentrantLock

/**
  * An implementation of a concurrent multi-set  
  * as a lazy list allowing for duplicates  
  */
class LazyDupList[T] extends ConcurrentMultiSet[T] {

  // Add sentinels to start and end
  private val head: Node = new Node(Integer.MIN_VALUE)
  head.next = new Node(Integer.MAX_VALUE)

  protected def validate(pred: Node, curr: Node): Boolean = {
    // TODO: Implement me!
    !pred.marked && !curr.marked && (pred.next eq curr)
  }

  /**
    * Add another element equal to item
    *
    * @return true iff element was not there already
    */
  def add(item: T): Boolean = {
    // TODO: Implement me!
    val key = item.hashCode
    while (true) {
      var pred = this.head
      var curr = head.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      val Node = new Node(item)
      pred.lock()
      try {
        curr.lock()
        try {
          if (validate(pred, curr)) {
            Node.next = curr
            pred.next = Node
            return true
          }
        } finally {
          curr.unlock()
        }
      } finally pred.unlock()
    }
    false
  }

  /**
    * Remove a single element equal to item
    *
    * @param item element to remove
    * @return true iff element's count >= 1
    */
  def remove(item: T): Boolean = {
    // TODO: Implement me!
    val key = item.hashCode
    while (true) {
      var pred = this.head
      var curr = head.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      while (true) {
        pred.lock()
        try {
          curr.lock()
          try {
            if (validate(pred, curr)) {
              if (curr.key != key) return false
              else { // absent
                if (curr.item == item) {
                  curr.marked = true // logically remove
                  pred.next = curr.next // physically remove
                  return true
                } else {
                  if (curr.next.key == curr.key) {
                    pred = curr
                    curr = curr.next
                  } else {
                    return false
                  }
                }
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
    }
    false
  }

  /**
    * Test whether element's count is >= 1
    */
  def contains(item: T): Boolean = {
    // TODO: Implement me!
    val key = item.hashCode
    var curr = this.head
    while (curr.key < key) curr = curr.next

    while (curr.key == key) {
      if (curr.item == item & !curr.marked) {
        return true
      } else {
        curr = curr.next
      }
    }
    false
//
//    while (curr.item != item & curr.key == curr.next.key) curr = curr.next
//    val c = curr.key == key && !curr.marked
//    if (!c) {
//      checkFailCounter.getAndIncrement()
//    }
//    c
  }

  override def count(item: T): Int = {
    val key = item.hashCode
    var curr = this.head
    while (curr.key < key) curr = curr.next
    var total = 0
    if (curr.key != key) {
      return 0
    }
    while (curr.key == key) {
      if (curr.item == item && !curr.marked) {
        total += 1
      }
      curr = curr.next
    }
    total
  }

  /**
    * list Node
    */
  private class Node(val item: T) {
    private val myLock = new ReentrantLock()

    private var _key: Option[Int] = None

    def key = if (_key.isEmpty) item.hashCode() else _key.get

    @volatile
    var next: Node = _
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