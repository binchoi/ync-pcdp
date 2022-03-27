package skiplists

import lists.ConcurrentSet

import java.util.concurrent.locks.ReentrantLock

/**
  * @author Yossi Lev and Maurice Herlihy 
  *         Scala transaction by Ilya Sergey
  */
class LazySkipList[T] extends ConcurrentSet[T] {

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


    // Keep trying until add
    // This function is in lieue of the usual while(true) loop
    while (true) {
      var skip = false
      val lFound = find(item, preds, succs)
      if (lFound != -1) {
        val nodeFound = succs(lFound)
        if (!nodeFound.marked) { // not marked
          while (!nodeFound.fullyLinked) {} // can be marked only if fully linked
          // so its unmarked and fully linked
          return false
        }
        skip = true
      }
      if (!skip) {
        var highestLocked = -1
        try {
          var pred, succ: Node = null
          var valid = true
          var level = 0
          while (valid && (level <= topLevel)) {
            pred = preds(level)
            succ = succs(level)
            pred.lock()
            highestLocked = level
            valid = !pred.marked && !succ.marked && (pred.next(level) eq succ)
            level = level + 1
          }
          if (!valid) {
            skip = true
          }
          if (!skip) {
            val newNode = new Node(item, topLevel)
            // first link succs
            for (lvl <- 0 to topLevel) {
              newNode.next(lvl) = succs(lvl)
            }
            // then link next fields of preds
            for (lvl <- 0 to topLevel) {
              preds(lvl).next(lvl) = newNode
            }
            newNode.fullyLinked = true
            return true
          }
        } finally {
          for (lvl <- 0 to highestLocked) {
            preds(lvl).unlock()
          }
        }
      }
    }

    throw new Exception("[add] This is unreachable")
  }

  override def remove(item: T): Boolean = {
    var victim: Node = null
    var isMarked = false
    var topLevel = -1
    val preds = new Array[Node](MAX_LEVEL + 1)
    val succs = new Array[Node](MAX_LEVEL + 1)

    while (true) {
      var skip = false
      val lFound = find(item, preds, succs)
      if (lFound != -1) {
        victim = succs(lFound) // found node
      }
      if (isMarked ||
        (lFound != -1 && // rest of test if found node
          (victim.fullyLinked // found linked marked node?
            && victim.topLevel == lFound
            && !victim.marked))) {
        if (!isMarked) {
          topLevel = victim.topLevel
          victim.lock()
          if (victim.marked) {
            victim.unlock()
            return false
          }
          victim.marked = true
          isMarked = true
        }
        var highestLocked = -1
        try {
          var pred, succ: Node = null
          var valid = true
          for (level <- 0 to topLevel if valid) {
            pred = preds(level)
            pred.lock()
            highestLocked = level
            valid = !pred.marked && (pred.next(level) eq victim)
          }
          if (!valid) {
            skip = true
          }
          if (!skip) {
            for (level <- topLevel to 0 by -1) {
              preds(level).next(level) = victim.next(level)
            }
            victim.unlock()
            return true
          }
        } finally {
          for (level <- 0 to highestLocked) {
            preds(level).unlock()
          }
        }
      } else {
        return false
      }
    }

    throw new Exception("[add] This is unreachable")
  }

  override def contains(item: T): Boolean = {
    val preds = new Array[Node](MAX_LEVEL + 1)
    val succs = new Array[Node](MAX_LEVEL + 1)
    val lFound = find(item, preds, succs)
    lFound != -1 && succs(lFound).fullyLinked && !succs(lFound).marked
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

    private val myLock = new ReentrantLock

    def key: Int = item.hashCode()

    final val next: Array[Node] = new Array[Node](height + 1)
    @volatile var marked = false
    @volatile var fullyLinked = false
    val topLevel = height

    def lock(): Unit = {
      myLock.lock()
    }

    /**
      * Unlock skip list.
      */
    def unlock(): Unit = {
      myLock.unlock()
    }
  }

}




