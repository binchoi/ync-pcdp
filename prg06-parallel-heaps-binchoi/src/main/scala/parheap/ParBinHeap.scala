package parheap

import scala.collection.immutable.Seq
import scala.collection.parallel.immutable.ParIterable
import scala.reflect.ClassTag

/**
  * Implementation of a parallel binary min-heap.
  *
  * The implicit parameter `Ordering[T]` allows to use it for standard data types,
  * such as Strings and Ints, which already have ordering defined on them
  */
class ParBinHeap[T: Ordering: ClassTag](private val maxSize: Int)
                    extends ParIterable[T] {

  // This is necessary to enable implicit `Ordering` on `T`s
  // Do not remove this import!
  import Ordering.Implicits._

  // Elements of the heap stored along with their priorities (Int)
  private var heap: Array[T] = new Array[T](maxSize) // heap.length = maxSize
  private var heapSize : Int = 0 // not fixed

  // TODO: DELETE LATER - THIS IS FOR TESTING ONLY
  def getArray(): Array[T] = {
    heap
  }

  // Create a new heap from a given array
  def this(array: Array[T]) = {
    this(array.length)
    heap = array.clone
    heapSize = array.length
    // Turn an array to a heap
    for (pos <- size / 2 to 0 by -1) {
      minHeapify(pos)
    }
  }

  //////////////////////////////////////////////////////////////////////
  // Internal Heap Methods
  //////////////////////////////////////////////////////////////////////
  // Return the position of the parent for the node currently at pos
  private def parent(pos: Int): (Int, T) = {
    if (pos==0) {
      (0, heap(pos))
    } else {
      val p_idx = (pos+1)/2 - 1
      (p_idx, heap(p_idx))
    }
  }

  // Return the position of the left child for the node currently at pos
  private def leftChild(pos: Int): Option[(Int, T)] = {
    val j = 2 * (pos + 1) - 1
    if (j<heapSize) {
      Some((j, heap(j)))
    } else None
  }

  // Return the position of the right child for the node currently at pos
  private def rightChild(pos: Int): Option[(Int, T)] = {
    val j = 2 * (pos + 1)
    if (j<heapSize) {
      Some((j, heap(j)))
    } else None
  }

  // true if the passed node is a leaf node 
  private def isLeaf(pos: Int): Boolean = {
    if (pos >= (size / 2) && pos <= size) {
      return true
    }
    false
  }

  // Function to heapify the node at pos 
  private def minHeapify(pos: Int): Unit = {
  val arr_len = maxSize
  assert(heapSize <= arr_len)
  if (isLeaf(pos) || pos >= heapSize) {} else {
    val ai = heap(pos)
    var smallest = (pos, heap(pos))
    val left_child = leftChild(pos)
    val right_child = rightChild(pos)

    // Should we swap with the left child?
    if (left_child.isDefined) {
      if (left_child.get._2 < smallest._2) {
        smallest = left_child.get
      } // smallest is still the original node
    }

    if (right_child.isDefined) {
      if (right_child.get._2 < smallest._2) {
//        println(s"child is SMALLER! ${right_child.get._2}")
        smallest = right_child.get
      }
    }

    if (smallest != (pos, ai)) {
      heap(pos) = smallest._2
      heap(smallest._1) = ai
      minHeapify(smallest._1)
    }
  }
}

  //////////////////////////////////////////////////////////////////////
  // Public heap Methods
  //////////////////////////////////////////////////////////////////////

  /**
    * Inserts element to the heap based on its priority 
    */
  def insert(elem: T): Unit = {
    // TODO: Implement me
    if (heapSize>=maxSize) {
      println("Overflow")
      return
    }
//    println(s"Trying to insert $elem")
    heap(heapSize) = elem
    var curr = heapSize
    // Trace up - to ensure correctness of the heap
    while (curr > 0 & parent(curr)._2 > heap(curr)) {
      var tmp_parent = parent(curr)
      heap(tmp_parent._1) = heap(curr)
      heap(curr) = tmp_parent._2

      curr = tmp_parent._1
    }
    heapSize += 1
  }

  /**
    * remove and return the minimum  
    */
  def removeMin: T = {
    if (heapSize<=0) {
      throw new Exception("Underflow")
    }
    val min_val = heap(0)
    heap(0) = heap(heapSize-1)
    heapSize -= 1
    minHeapify(0)

    min_val
  }

  //////////////////////////////////////////////////////////////////////
  // Parallel Collection Methods
  //////////////////////////////////////////////////////////////////////

  /**
    * Returns a sequence of elements currently in the binary heap
    */
  override def seq: Seq[T] = heap.slice(0, heapSize).toIndexedSeq
  /**
    * Returns a splitter for the binary heap.
    *
    * Use your knowledge of the heap structure to make the best splits.
    *
    */
  override def splitter: ParBinHeapSplitter[T] = {
    new ParBinHeapSplitter[T](heap, heapSize, 0, heapSize)
  }

  /**
    * Returns the number of elements currently in the heap
    */
  override def size: Int = heapSize
}

// Reserve for removeMin
//    while (!isLeaf(curr)) {
//      // Then, it must at least have a left child
//      val left_c = leftChild(curr)
//      // If curr has only left child...
//      if (left_c.get._1 + 1 == heapSize) {val swapCandidate = left_c}
//      else {
//        // If curr has right child as well...
//        val right_c = rightChild(curr)
//        if (left_c.get._2 < right_c.get._2) {
//          val swapCandidate = left_c
//        } else {
//          val swapCandidate = right_c
//        }
//      }
//    }
