package sorting

import sorting.ArrayUtil.swap

/**
  * Simple sequential in-place QuickSort
  */
object SimpleQuickSort extends Sorting {

  // This is necessary to enable implicit `Ordering` on `Int`s
  // Do not remove this import!
  import Ordering.Implicits._

  /**
    * A standard sub-routine of QuickSort that partitions an array
    * into two parts with regard to the pivot 
    * (<= pivot and > pivot, correspondingly ).
    */
  def partition[T: Ordering](arr: Array[T], lo: Int, hi: Int): Int = {
    // Implemented partition of a sub-array via a pivot
    // Choice of Pivot: Sedgewick method -- median of first, middle, last element of arr
    val midIndex = (lo + hi) / 2     // default Int division = floor division
    if (arr(midIndex) < arr(lo)) {
      swap(arr, lo, midIndex)
    }
    if (arr(hi) < arr(lo)) {
      swap(arr, hi, lo)              // both arr(hi), arr(midIndex) > arr(lo) now
    }
    if (arr(midIndex) < arr(hi)) {   // min(arr(hi), arr(midIndex)) = median-of-three (=> pivot value)
      swap(arr, midIndex, hi)
    }
    val pivotVal = arr(hi)

    // Standard Lomuto Partition Scheme
    var i = lo - 1
    // Iterate through each element while considering worst case where all
    // elements from arr(lo) to arr(hi) are identical
    var allSame = true
    for (j <- lo until hi) {
      if (arr(j) <= pivotVal) {
        i += 1
        swap(arr, i, j)
      }
      allSame = allSame & (pivotVal == arr(j))
    }
    if (allSame) {
      return -1         // Special return value to signal to sortHelper that no
    }                   // more sorting is required for this sub-array
    swap(arr, i+1, hi)  // move pivot to its appropriate location, and return
    i+1                 // the idx of the pivot
  }

  def sortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int): Unit = {
    if (lo < hi) {      // check sub-array is valid (contains more than one elt)
      val q = partition(arr, lo, hi)
      if (q == -1) {    // if all elements of sub-array were identical, end execution
        return
      }
      sortHelper(arr, lo, q-1) // left of pivot
      sortHelper(arr, q+1, hi) // right of pivot
    }
  }

  def sort[T: Ordering](arr: Array[T]): Unit = {
    // Implemented by relying on the `partition` procedure.
    val lenArr = arr.length
    sortHelper(arr, 0, lenArr-1)
  }

  /**
    * Returns the name of the sorting method 
    */
  override def getName = "SimpleSort"
}
