package sorting

import pool.ThreadPool
import sorting.SimpleQuickSort.partition
import math.pow
import math.sqrt

/**
  * Hybrid sorting. Unleash your creativity here!
  */
object HybridQuickSort extends Sorting {

  /**
    * Returns the name of the sorting method 
    */
  override def getName = "HybridSort"

  val NUM_THREADS: Int = 2 // The number of workers used in parallel can be changed

  def hybridSortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int, pool: ThreadPool): Unit = {
    if (lo < hi) {
      val q = partition(arr, lo, hi)
      if (q == -1) {
        return
      }
      pool.async(_=>SimpleQuickSort.sortHelper(arr, lo, q-1))
      pool.async(_=>SimpleQuickSort.sortHelper(arr, q+1, hi))
    }
  }

  def sort[T: Ordering](arr: Array[T]): Unit = {
    val pool = new ThreadPool(NUM_THREADS)
    val lenArr = arr.length
    // Use `startAndWait()` to make sure that the task (and its sub-tasks) are completed
    // by the time this call returns
    pool.startAndWait(_=>hybridSortHelper(arr, 0, lenArr-1, pool))
    // Do not forget to shut down the pool after the array is sorted
    pool.shutdown()
  }
}
//
//  def hybridSortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int, pool: ThreadPool): Unit = {
//    var hiVar = hi
//    while (lo < hiVar) {
//      val q = partition(arr, lo, hiVar)
//      if (q == -1) {
//        return
//      }
//      val tmp = hiVar // bug
//      pool.async(_=>hybridSortHelper(arr, q+1, tmp, pool))
////      hybridSortHelper(arr, q+1, tmp, pool)
//      hiVar = q-1
//    }
//  }

//  def sort[T: Ordering](arr: Array[T]): Unit = {
//    // TODO: The Hybrid sorting should take the best out of concurrent and sequential worlds
//    //       Feel free to experiment with the design and make sure to describe your intuition
//
//    val pool = new ThreadPool(NUM_THREADS)
//    val lenArr = arr.length
//    pool.startAndWait(_=>hybridSortHelper(arr, 0, lenArr-1, pool))
//    pool.shutdown()
//  }


// For threads > 2
// counter = pow(2, (sqrt(NUM_THREADS).toInt-1)).toInt - 1
//def hybridSortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int, pool: ThreadPool): Unit = {
//  if (lo < hi) {
//  val q = partition(arr, lo, hi)
//  if (q == -1) {
//  return
//  }
//  if (counter>=1) {
//  pool.async(_=>hybridSortHelper(arr, lo, q-1, pool))
//  pool.async(_=>hybridSortHelper(arr, q+1, hi, pool))
//  counter = counter-1
//  }
//  if (counter<1) {
//  pool.async(_=>SimpleQuickSort.sortHelper(arr, lo, q-1))
//  pool.async(_=>SimpleQuickSort.sortHelper(arr, q+1, hi))
//  }
//
//  pool.async(_=>SimpleQuickSort.sortHelper(arr, lo, q-1))
//  pool.async(_=>SimpleQuickSort.sortHelper(arr, q+1, hi))
//  }
//  }

