package futures.sorting

import java.util.concurrent.{Executors, Future}

import futures.sorting.ArrayUtil._

/**
 * Example 4 
 * Hybrid sorting. Unleash your creativity here!
 */
object FutureSort extends Sorting {

  /**
   * Returns the name of the sorting method 
   */
  override def getName = "FutureSort"

  def sort[T: Ordering](arr: Array[T]): Unit = {
    val pool = Executors.newCachedThreadPool()

    // Alternatively, try
    //    val pool = Executors.newWorkStealingPool()

    //    TODO: Why the following would not work?
    //    Hint: The problem is in _explicit synchronisation_ below.
    //val pool = Executors.newFixedThreadPool(4)

    def innerSort(lo: Int, hi: Int): Unit = {
      if (hi - lo <= 1) {
        // Do nothing
      } else {
        val mid = partition(arr, lo, hi)
        if (hi - lo <= 100000) {
          innerSort(lo, mid)
          innerSort(mid, hi)
        } else {
          // TODO: Parallel sorting
          val f1: Future[_] = pool.submit(new Runnable {
            override def run(): Unit = innerSort(lo, mid)
          })
          val f2: Future[_] = pool.submit(new Runnable {
            override def run(): Unit = innerSort(mid, hi)
          })

          // Wait for the concurrent sorts to end
          f1.get()
          f2.get()
        }
      }
    }

    try {
      val f = pool.submit(new Runnable {
        override def run(): Unit = {
          innerSort(0, arr.length)
        }
      })
      f.get()
    } finally {
      // Don't forget to shut down the pool
      pool.shutdown()
    }

  }

}
