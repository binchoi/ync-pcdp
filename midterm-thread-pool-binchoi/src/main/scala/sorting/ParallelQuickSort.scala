package sorting

import sorting.ArrayUtil.swap
import sorting.SimpleQuickSort.partition


/**
  * Parallel in-place QuickSort
  */
object ParallelQuickSort extends Sorting {

  /**
    * Returns the name of the sorting method 
    */
  override def getName = "ParallelSort"

  class parallelSortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int) extends Thread {
    override def run(): Unit = {
      if (lo < hi) {
        val q = partition(arr, lo, hi)
        if (q == -1) {
          return
        }
        val leftThread = new parallelSortHelper[T](arr, lo, q-1)
        val rightThread = new parallelSortHelper[T](arr, q+1, hi)

        leftThread.start()
        rightThread.start()

        leftThread.join()
        rightThread.join()
      }
    }
  }

  def sort[T: Ordering](arr: Array[T]): Unit = {
    // TODO: Implement a version of sort that creates concurrent threads for
    //       sorting recursive sub-arrays in parallel.
    // TODO: You might want to reuse some functions from the sequential version
    val lenArr = arr.length
    val mainThread = new parallelSortHelper[T](arr, 0, lenArr-1)
    mainThread.start()
    mainThread.join()
  }
}
