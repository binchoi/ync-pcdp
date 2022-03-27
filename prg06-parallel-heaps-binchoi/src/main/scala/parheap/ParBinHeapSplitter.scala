package parheap

import scala.collection.parallel.IterableSplitter
import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
/**
  * A splitter for parallel binary heaps
  *
  * @param heap           A min-heapified array
  * @param heapSize       The size of the heap
  * @param rootPos        The root of the current sub-heap
  * @param remainingGuess An upper-bound on the size of the current sub-heap
  * @tparam T The type of elements contained within the heap
  */
class ParBinHeapSplitter[T: ClassTag](private val heap: Array[T],
                                      private val heapSize: Int,
                                      private val rootPos: Int,
                                      private val remainingGuess: Int)
  extends IterableSplitter[T] {

  // Current position in the sub-heap to process
  private var pos: Int = rootPos
  private var remainingNum: Int = remainingGuess
  private var left_lim_idx: Int = rootPos  // after fst next() => leftChild(rootPos)
  private var right_lim_idx: Int = rootPos // after fst next() => rightChild(rootPos)

  /**
    * Let's define some helpful helper functions to navigate the heap array
    */

  // All indices must be < heapSize - need to check after calling fxn
  def leftChild(idx: Int): Int = 2 * (idx + 1) - 1

  def rightChild(idx: Int): Int = 2 * (idx + 1)

  /**
    * Creating more splitters
    * This better be an O(1) operation!
    */

  // Adapting the structure of ParStringSplitter,
  override def split: Seq[ParBinHeapSplitter[T]] = {
    if (remainingNum > 1 & pos < heapSize) {
      var ss = Seq[ParBinHeapSplitter[T]](new ParBinHeapSplitter[T](heap, heapSize, rootPos, 1))
      val left_cand_root = leftChild(pos)
      val right_cand_root = rightChild(pos)
      if (left_cand_root < heapSize) {
        ss = ss :+ new ParBinHeapSplitter[T](heap, heapSize, left_cand_root, remainingNum)
      }
      if (right_cand_root < heapSize) {
        ss = ss :+ new ParBinHeapSplitter[T](heap, heapSize, right_cand_root, remainingNum)
      }
      ss
    } else Seq(this)
  }

  /** Creating a copy of the splitter
    * This should also work in a constant time!
    */
  override def dup: ParBinHeapSplitter[T] = new ParBinHeapSplitter[T](heap, heapSize, rootPos, remainingGuess)

  /**
    * The number of remaining elements in the sub-heap to process
    *
    * This only needs to be an upper-bound, not an exact figure.
    * Take advantage of this here and use remainingGuess parameter,
    * because calculating the exact number of elements in
    * the sub-tree would be expensive.
    */
  override def remaining: Int = remainingGuess

  // Misc. helper function for hasNext - archaic
//  def hasNextHelper: Option[Int] = {
//    if (pos == right_lim_idx) {
//      val left_start = leftChild(left_lim_idx)
//      if (left_start < heapSize) Some(left_start)
//      else None
//    }
//    else {
//      if (pos + 1 < heapSize) Some(pos + 1)
//      else None
//    }
//  }
  /**
    * Checks, if this subheap has more elements to process
    */

  override def hasNext: Boolean = {
    if (pos > right_lim_idx-1) {
      val left_start = leftChild(left_lim_idx)
      if (left_start < heapSize && remainingNum > 0) true
      else false
    }
    else {
      if (pos + 1 < heapSize && remainingNum > 0) true
      else false
    }
  }

  /**
    * The next element in the "sub-heap" to process
    */
  override def next(): T = {
    val res = heap(pos)

    if (pos > right_lim_idx - 1) {
      left_lim_idx = leftChild(left_lim_idx)
      right_lim_idx = rightChild(right_lim_idx)
      pos = left_lim_idx
    } else pos += 1

    remainingNum -= 1
    res
  }
}



// Misc attempts and failed tries

//  private var currLevel: Int = 1
//  private var left_on_lvl: Int = 2
//  private var prev_lvl_idx: List[Int] = List(rootPos)
//  private var curr_lvl_idx: List[Int] = List()
//  @volatile
//  private var next_idx: Int = -1
// Attempt 3: next()
//// TODO: Implement me
//val res = heap(pos)
//pos = next_idx
//left_on_lvl -= 1
//remainingNum -= 1
//res
// ATTEMPT 3: hasNext
//    if (left_on_lvl == 0) {
//      currLevel += 1
//      left_on_lvl = math.pow(2, currLevel).toInt
//      prev_lvl_idx = curr_lvl_idx
//      curr_lvl_idx = List()
//    }
//
//    val parent_idx = prev_lvl_idx((math.pow(2, currLevel).toInt - left_on_lvl - 1) / 2)
//    var next_child = -1
//
//    if (left_on_lvl % 2 == 0) {
//      next_child = leftChild(parent_idx)
//    } else {
//      next_child = rightChild(parent_idx)
//    }
//
//    if (next_child > heapSize) false
//    else {
//      next_idx = next_child
//      curr_lvl_idx = curr_lvl_idx :+ next_child
//      true
//    }
// VERY STRANGE BUG - I restarted the whole file
//package parheap
//
//import scala.collection.parallel.IterableSplitter
//import scala.math.{log, log10}
//import scala.reflect.ClassTag
//
///**
//  * @author Ilya Sergey
//  */
///**
//  * A splitter for parallel binary heaps
//  *
//  * @param heap           A min-heapified array
//  * @param heapSize       The size of the heap
//  * @param rootPos        The root of the current sub-heap
//  * @param remainingGuess An upper-bound on the size of the current sub-heap
//  * @tparam T The type of elements contained within the heap
//  */
//class ParBinHeapSplitter[T: ClassTag](private val heap: Array[T], // could be sub-heap
//                                      private val heapSize: Int, // constant
//                                      private val rootPos: Int, // initially 0
//                                      private val remainingGuess: Int) // initially heapSize
//  extends IterableSplitter[T] {
//
//  // Current position in the sub-heap to process
//  private var pos: Int = rootPos
//  private var levelMap = scala.collection.mutable.Map[Int, List[Int]](0 -> List(rootPos))
//
//  private var curr_level = 1
//  private var seen_at_curr_level = 0
//  var finished = false
//  var numRemaining: Int = remainingGuess
//
//  private def leftChild(pos: Int): (Boolean, Int) = {
//    val j = 2 * (pos + 1) - 1
//    (j<heapSize, j)
//  }
//
//  private def rightChild(pos: Int): (Boolean, Int)  = {
//    val j = 2 * (pos + 1)
//    (j<heapSize, j)
//  }
//
//  private def isLeaf(pos: Int): Boolean = {
//    (pos >= (heapSize / 2) && pos <= heapSize)
//  }
//
//  private def guessRemainingSizes(): Int = {
//    (remainingGuess/2)+1
//    // Not accurate upper bound (refer to comment below for accurate remainingSize) but for time complexity
//  }
//
//  /**
//    * Creating more splitters
//    * This better be an O(1) operation!
//    */
//  override def split: Seq[ParBinHeapSplitter[T]] = {
//    if (remainingGuess <= 1 || isLeaf(rootPos)) {
//      Seq(new ParBinHeapSplitter[T](heap, heapSize, rootPos, 1))
//    } else {
//      var ss = Seq[ParBinHeapSplitter[T]]()
//      if (leftChild(rootPos)._1) {
//        ss = ss :+ new ParBinHeapSplitter[T](heap, heapSize, leftChild(rootPos)._2, guessRemainingSizes())
//      }
//      if (rightChild(rootPos)._1) {
//        ss = ss :+ new ParBinHeapSplitter[T](heap, heapSize, rightChild(rootPos)._2, guessRemainingSizes()) // println(s"newsplitter at -  ${value._1}");
//      }
//      ss :+ new ParBinHeapSplitter[T](heap, heapSize, rootPos, 1)
//    }
//  }
//
//  /** Creating a copy of the splitter
//    * This should also work in a constant time!
//    */
//  override def dup: ParBinHeapSplitter[T] = {
//    new ParBinHeapSplitter[T](heap, heapSize, rootPos, remainingGuess)
//  }
//
//  /**
//    * The number of remaining elements in the sub-heap to process
//    *
//    * This only needs to be an upper-bound, not an exact figure.
//    * Take advantage of this here and use remainingGuess parameter,
//    * because calculating the exact number of elements in
//    * the sub-tree would be expensive.
//    */
//  override def remaining: Int = {
//    numRemaining
//  }
//
//  /**
//    * Checks, if this subheap has more elements to process
//    */
//  override def hasNext: Boolean = {
//    if (remainingGuess==1 & finished) {return false}
//
//    val parent_of_next = levelMap(curr_level - 1)(seen_at_curr_level / 2) // floor division
//    if (seen_at_curr_level % 2 == 0) {
//      (leftChild(parent_of_next)._1)
//    } else {
//      (rightChild(parent_of_next)._1)
//    }
//  }
//
//  /**
//    * The next element in the "sub-heap" to process
//    */
//  override def next(): T = {
//    val res = heap(pos)
//    val parent_of_next = levelMap(curr_level-1)(seen_at_curr_level/2) // floor division
//    if (seen_at_curr_level%2==0) {
//      if (leftChild(parent_of_next)._1) {
//        pos = leftChild(parent_of_next)._2
//      }
//    } else {
//      if (rightChild(parent_of_next)._1) {
//        pos = rightChild(parent_of_next)._2
//      }
//    }
//
//    if (seen_at_curr_level==0) {
//      levelMap(curr_level) = List(pos)
//    } else {
//      levelMap.update(curr_level, levelMap(curr_level) :+ pos)
//    }
//
//    seen_at_curr_level += 1
//    if (seen_at_curr_level == math.pow(2, curr_level)) {
//      seen_at_curr_level = 0
//      curr_level += 1
//    }
//
//    finished = true
//    numRemaining -= 1
//    res
//  }
//}

//      Seq(this) -> Why does this deadlock for parbinheapsplitters' whose rootpos is a leaf node? -> it keeps trying to split!


//    val log_res = log10(remainingGuess+1)/log10(2.0)
//    val completeHeight = math.floor(log_res).toInt
//    val tallestHeight = math.ceil(log_res).toInt
//    val baseNum = math.pow(2, completeHeight-1).toInt - 1
//    val lastRow = remainingGuess - math.pow(2, tallestHeight-1).toInt
//    val leftAdditional = lastRow.min(math.pow(2,completeHeight).toInt)
//    val rightAdditional = 0.max(lastRow - math.pow(2,completeHeight).toInt)
// sum of baseNum and leftAdd -> left's RemainingGuess
// sum of baseNum and rightAdd -> right's RemainingGuess

//    // size of left splitter, size of right splitter
//    (baseNum+leftAdditional, baseNum+rightAdditional)


