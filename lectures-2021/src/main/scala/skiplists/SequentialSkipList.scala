package skiplists

import lists.ConcurrentSet

/**
  * @author Ilya Sergey
  */
class SequentialSkipList[T] extends ConcurrentSet[T] {

  val MAX_LEVEL: Int = 32

  var randomSeed: Int = System.currentTimeMillis.toInt | 0x010000
  private val head = mkSentinel(Integer.MIN_VALUE)
  private val tail = mkSentinel(Integer.MAX_VALUE)

  // Initial structure between the sentinels
  for (i <- head.next.indices) {
    head.next(i) = tail
  }

  override def add(item: T): Boolean = {
    val topLevel = randomLevel()
    val preds = new Array[Node](MAX_LEVEL + 1)
    val succs = new Array[Node](MAX_LEVEL + 1)

    val lFound = find(item, preds, succs)

    if (lFound != -1) {
      return false
    }

    val newNode = new Node(item, topLevel)
    // first link succs
    for (lvl <- 0 to topLevel) {
      newNode.next(lvl) = succs(lvl)
    }
    // then link next fields of preds
    for (lvl <- 0 to topLevel) {
      preds(lvl).next(lvl) = newNode
    }
    true
  }

  override def remove(item: T): Boolean = {
    val preds = new Array[Node](MAX_LEVEL + 1)
    val succs = new Array[Node](MAX_LEVEL + 1)
    val lFound = find(item, preds, succs)
    if (lFound != -1) {
      val victim = succs(lFound) // found node
      val topLevel = victim.topLevel
      for (level <- topLevel to 0 by -1) {
        preds(level).next(level) = victim.next(level)
      }
      true
    } else {
      false
    }
  }

  override def contains(item: T): Boolean = {
    val preds = new Array[Node](MAX_LEVEL + 1)
    val succs = new Array[Node](MAX_LEVEL + 1)
    val lFound = find(item, preds, succs)
    lFound != -1
  }

  ////////////////////////////////////////////////////////////////////
  //  Finding an element
  ////////////////////////////////////////////////////////////////////

  /**
    * Find predecessors and successors for the element T,
    * going by different levels
    */
  def find(x: T, preds: Array[Node], succs: Array[Node]): Int = {
    val v = x.hashCode()
    var lFound = -1
    var pred = head
    for (level <- MAX_LEVEL to 0 by -1) {
      var curr = pred.next(level)
      while (v > curr.key) {
        pred = curr
        curr = pred.next(level)
      }
      if (lFound == -1 && v == curr.key) {
        lFound = level
      }
      preds(level) = pred
      succs(level) = curr
    }
    lFound
  }

  ////////////////////////////////////////////////////////////////////
  //  Randomness
  ////////////////////////////////////////////////////////////////////

  /**
    * Generate a random level to insert a node into 
    */
  private def randomLevel(): Int = {
    var x = randomSeed
    x ^= x << 13
    x ^= x >>> 17
    x ^= x << 5
    randomSeed = x
    if ((x & 0x8001) != 0) { // test highest and lowest bits
      return 0
    }
    var level = 1

    while (({
      x >>>= 1
      x
    } & 1) != 0) {
      level += 1
      level
    }
    Math.min(level, MAX_LEVEL - 1)
  }


  ////////////////////////////////////////////////////////////////////
  //  Managing SkipList Nodes
  ////////////////////////////////////////////////////////////////////

  /**
    * Create a sentinel node with a given key 
    */
  private def mkSentinel(_key: Int): Node =
    new Node(null.asInstanceOf[T], MAX_LEVEL) {
      override def key = _key
    }

  /**
    * A node of a lazy skip list
    */
  private class Node(val item: T, private val height: Int) {
    def key: Int = item.hashCode()

    final val next: Array[Node] = new Array[Node](height + 1)
    val topLevel = height
  }

}

