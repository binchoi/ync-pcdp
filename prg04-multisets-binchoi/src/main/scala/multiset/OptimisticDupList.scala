package multiset

import java.util.concurrent.locks.ReentrantLock

/**
  * An implementation of a concurrent multi-set
  * as an optimistic list allowing for duplicates
  */
class OptimisticDupList[T] extends ConcurrentMultiSet[T] {

  private val head: Node = new Node(Integer.MIN_VALUE)
  head.next = new Node(Integer.MAX_VALUE) // tail

  private val lock = new ReentrantLock

  protected def validate(pred: Node, curr: Node): Boolean = {
    // TODO: Implement me!
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
    * Add another element equal to item
    *
    * @return true iff element was not there already
    */
  def add(item: T): Boolean = {
    // TODO: Implement me!
    val key = item.hashCode()
    //    println(s"$item and $key") ///////////////////////////////////////////
    while (true) {
      var pred = this.head
      var curr = pred.next
      while (curr.key < key) {
        pred = curr
        curr = curr.next
      }
      val entry = new Node(item) // can move down to the else-branch if I want to optimize space (but comp. on time)
      pred.lock()
      curr.lock()
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present - then append to the list of items - could be not same item (hash collision)
            if (curr.itemsDict.contains(item)) {
              curr.itemsDict(item) += 1 // curr.itemsDict(item) // increment by one
            } else { // in case of different item (hash collision)
              curr.itemsDict += (item -> 1)
            }
            return true
          } else { // not present - unique key
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
    * Remove a single element equal to item
    *
    * @param item element to remove
    * @return true iff element's count >= 1
    */
  def remove(item: T): Boolean = {
    // TODO: Implement me!
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
          if (curr.key == key & curr.itemsDict.contains(item)) { // present in list
            if (curr.itemsDict(item)>0) {
              curr.itemsDict(item) -= 1
            } else {
              return false
            }

            if (curr.itemsDict.values.sum == 0) {
              pred.next = curr.next
              return true
            } else {
              return true // don't reroute the list
            }
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
    * Test whether element's count is >= 1
    */
  def contains(item: T): Boolean = {
    // TODO: Implement me!
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
          if (curr.key == key & curr.itemsDict.contains(item)) {
            if (curr.itemsDict(item)>0) {
              return true
            } else {
              return false
            }
          } else {
            return false
          }
          //          return curr.key == key
        } // else { // if cannot validate
        //          checkFailCounter.getAndIncrement() // should check next one maybe same hash value
        //        }
      } finally { // always unlock
        pred.unlock()
        curr.unlock()
      }
    }
    false
  }

  /**
    * A count of items equals to item in the list
    */
  def count(item: T): Int = {
    // TODO: Implement me!
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
          if (curr.key == key & curr.itemsDict.contains(item)) {
            return curr.itemsDict(item)
          } else {
            return 0
          }
        }
      } finally { // always unlock
        pred.unlock()
        curr.unlock()
      }
    }
    0
  }

  /**
    * list Node
    */
  private class Node(val item: T) {
    private val myLock = new ReentrantLock()

    private var _key: Option[Int] = None

    val itemsDict: scala.collection.mutable.Map[T, Int] = scala.collection.mutable.Map(item -> 1)

    def key = if (_key.isEmpty) item.hashCode() else _key.get

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
// ANOTHER METHOD THAT DOESNt MODIFY NODE 
//package multiset
//
//import java.util.concurrent.locks.ReentrantLock
//
///**
//  * An implementation of a concurrent multi-set
//  * as an optimistic list allowing for duplicates
//  */
//class OptimisticDupList[T] extends ConcurrentMultiSet[T] {
//
//  private val head: Node = new Node(Integer.MIN_VALUE)
//  head.next = new Node(Integer.MAX_VALUE) // tail
//
//  private val lock = new ReentrantLock
//
//  protected def validate(pred: Node, curr: Node): Boolean = {
//    var entry = head
//    while (entry.key <= pred.key) {
//      if (entry eq pred) {
//        // Checking for reference equality
//        return pred.next eq curr
//      }
//      entry = entry.next
//    }
//    false
//  }
//
//  /**
//    * Add another element equal to item
//    *
//    * @return true iff element was not there already
//    */
//  def add(item: T): Boolean = {
//    // TODO: Implement me!
//    val key = item.hashCode()
//    while (true) {
//      var pred = this.head
//      var curr = pred.next
//      while (curr.key < key) {
//        pred = curr
//        curr = curr.next
//      }
//      val entry = new Node(item) // can move down to the else-branch if I want to optimize space (but comp. on time)
//      pred.lock()
//      curr.lock()
//      try {
//        if (validate(pred, curr)) {
//          entry.next = curr
//          pred.next = entry
//          return true
//        }
//      } finally { // always unlock
//        pred.unlock()
//        curr.unlock()
//      }
//    }
//    false
//  }
//
//  /**
//    * Remove a single element equal to item
//    *
//    * @param item element to remove
//    * @return true iff element's count >= 1
//    */
//  def remove(item: T): Boolean = {
//    // TODO: Implement me!
//    val key = item.hashCode()
//    while (true) {
//      var pred = this.head
//      var curr = pred.next
//      while (curr.key < key) {
//        pred = curr
//        curr = curr.next
//      }
//      while (curr.item != item) {
//        pred = curr
//        curr = curr.next
//      }
//
//      pred.lock()
//      curr.lock()
//      try {
//        if (validate(pred, curr)) {
//          if (curr.key == key & curr.item == item) { // present in list
//            pred.next = curr.next
//            return true
//          } else { // not present in list
//            return false
//          }
//        }
//      } finally { // always unlock
//        pred.unlock()
//        curr.unlock()
//      }
//    }
//    false
//  }
//
//  /**
//    * Test whether element's count is >= 1
//    */
//  def contains(item: T): Boolean = {
//    // TODO: Implement me!
//    val key = item.hashCode()
//    while (true) {
//      var pred = this.head
//      // sentinel node;
//      var curr = pred.next
//      while (curr.key < key) {
//        pred = curr
//        curr = curr.next
//      }
//      while (curr.key == key & curr.item != item) {
//        pred = curr
//        curr = curr.next
//      }
//      try {
//        pred.lock()
//        curr.lock()
//        if (curr.key!= key) {
//          return false
//        }
//        if (validate(pred, curr)) {
//          return (curr.key == key & curr.item == item)
//        }
//      } finally { // always unlock
//        pred.unlock()
//        curr.unlock()
//      }
//    }
//    false
//  }
//
//  /**
//    * A count of items equals to item in the list
//    */
//  def count(item: T): Int = {
//    val key = item.hashCode()
//    while (true) {
//      var counter = 0
//      var pred = this.head
//      // sentinel node;
//      var curr = pred.next
//      while (curr.key < key) {
//        pred = curr
//        curr = curr.next
//      }
//      while (curr.key == key & curr.item != item) {
//        pred = curr
//        curr = curr.next
//      }
//      while (curr.key == key) {
//        try {
//          pred.lock()
//          curr.lock()
//          if (curr.key!= key) {
//            return counter
//          }
//          if (validate(pred, curr)) {
//            if (curr.key == key & curr.item == item) {
//              counter += 1
//            }
//            pred = curr
//            curr = curr.next
//          }
//        } finally { // always unlock
//          pred.unlock()
//          curr.unlock()
//        }
//      }
//      counter
//    }
//    0
//  }
//
//  /**
//    * list Node
//    */
//  private class Node(val item: T) {
//    private val myLock = new ReentrantLock()
//
//    private var _key: Option[Int] = None
//
//    def key = if (_key.isEmpty) item.hashCode() else _key.get
//
//    @volatile
//    var next: Node = _
//
//    def lock(): Unit = myLock.lock()
//
//    def unlock(): Unit = myLock.unlock()
//
//    /**
//      * Constructor for sentinel Node
//      *
//      * @param key should be min or max int value
//      */
//    def this(key: Int) {
//      this(null.asInstanceOf[T])
//      this._key = Some(key)
//    }
//  }
//
//}
//
//
//

// WRONG ATTEMPT

