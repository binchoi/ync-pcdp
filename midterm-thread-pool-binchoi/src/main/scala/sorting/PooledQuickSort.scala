package sorting

import pool.ThreadPool
import sorting.SimpleQuickSort.partition

/**
  * In-place QuickSort using Thread Pool
  */
object PooledQuickSort extends Sorting {

  val NUM_THREADS = 2


  def sortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int, pool: ThreadPool): Unit = {
    if (lo < hi) {
      val q = partition(arr, lo, hi)
      if (q == -1) {
        return
      }
    // Use `async()` to allocate create more non-blocking tasks.
      pool.async(_=>sortHelper(arr, lo, q-1, pool))
      pool.async(_=>sortHelper(arr, q+1, hi, pool))
    }
  }

  def sort[T: Ordering](arr: Array[T]): Unit = {
    val pool = new ThreadPool(NUM_THREADS)
    val lenArr = arr.length
    // Use `startAndWait()` to make sure that the task (and its sub-tasks) are completed
    // by the time this call returns
    pool.startAndWait(_=>sortHelper(arr, 0, lenArr-1, pool))
    // Do not forget to shut down the pool after the array is sorted
    pool.shutdown()
  }

  /**
    * Returns the name of the sorting method
    */
  override def getName = "PooledSort"
}

//  def sortHelper[T: Ordering](arr: Array[T], lo: Int, hi: Int, pool: ThreadPool): Unit = {
//    if (lo < hi) {
//      val q = partition(arr, lo, hi)
//      if (q == -1) {
//        return
//      }
//      // Use `async()` to allocate create more non-blocking tasks.
//      pool.async(_=>SimpleQuickSort.sortHelper(arr, lo, q-1))
//      pool.async(_=>SimpleQuickSort.sortHelper(arr, q+1, hi))
//    }
//  }
//
//    // Implement a version of sort that, instead of spawning/joining new threads
//    //       allocates tasks in the Thread Pool.
//
//  def sort[T: Ordering](arr: Array[T]): Unit = {
//    val pool = new ThreadPool(NUM_THREADS)
//    val lenArr = arr.length
//    // Use `startAndWait()` to make sure that the task (and its sub-tasks) are completed
//    // by the time this call returns
//    pool.startAndWait(_=>sortHelper(arr, 0, lenArr-1, pool))
//    // Do not forget to shut down the pool after the array is sorted
//    pool.shutdown()
//  }

