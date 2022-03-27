package lists

import java.util.concurrent.atomic.AtomicMarkableReference

class LockFreeList[T] extends ConcurrentSet[T] {

  val head: Node = new Node(Integer.MIN_VALUE)
  val tail = new Node(Integer.MAX_VALUE)
  while (!head.next.compareAndSet(null, tail, false, false)) {}

  /**
    * Add an element.
    *
    * @param item element to add
    * @return true iff element was not there already
    */
  def add(item: T): Boolean = {
    val key = item.hashCode
    val splice = false
    while (true) { // find predecessor and curren entries
      val window = find(head, key)
      val pred = window.pred
      val curr = window.curr
      // is the key present?
      if (curr.key == key) {
        return false
      } else { // splice in new node
        val node = new Node(item)
        node.next = new AtomicMarkableReference(curr, false)
        if (pred.next.compareAndSet(curr, node, false, false)) {
          return true
        }
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
    val key = item.hashCode
    var snip = false
    while (true) {
      val window = find(head, key)
      val pred = window.pred
      val curr = window.curr
      if (curr.key != key) return false
      else { // snip out matching node
        val succ = curr.next.getReference
        snip = curr.next.attemptMark(succ, true)
        if (!snip) {} else {
          pred.next.compareAndSet(curr, succ, false, false)
          return true
        }
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
    val window = find(head, key)
    val pred = window.pred
    val curr = window.curr
    val c = curr.key == key
    if (!c) {
      checkFailCounter.getAndIncrement() 
    }
    c
  }

  /**
    * list node
    */
  class Node(val item: T) {
    private var _key : Option[Int] = None
    def key = if (_key.isEmpty) item.hashCode() else _key.get

    var next = new AtomicMarkableReference[Node](null, false)

    def this(key: Int) {
      this(null.asInstanceOf[T])
      this._key = Some(key)
    }
  }

  /**
    * Pair of adjacent list entries.
    */
  class Window(var pred: Node, var curr: Node)

  /**
    * If element is present, returns node and predecessor. If absent, returns
    * node with least larger key.
    *
    * @param head start of list
    * @param key  key to search for
    * @return If element is present, returns node and predecessor. If absent, returns
    *         node with least larger key.
    */
  def find(head: Node, key: Int): Window = {
    var pred, curr, succ: Node = null
    val marked = Array(false) // is curr marked?
    var snip = false
    while (true) {
      var retry = false
      pred = head
      curr = pred.next.getReference
      while (!retry) {
        succ = curr.next.get(marked)
        while (marked(0) && !retry) { // replace curr if marked
          snip = pred.next.compareAndSet(curr, succ, false, false)
          if (!snip) {
            retry = true
          } else {
            curr = pred.next.getReference
            succ = curr.next.get(marked)
          }
        }
        if (!retry) {
          if (curr.key >= key) {
            return new Window(pred, curr)
          }
          pred = curr
          curr = succ
        }
      }
    }
    null
  }
}




