package examples

import java.util.concurrent.locks.ReentrantLock

import pool.ThreadPool

/**
  * @author Ilya Sergey
  */
object StartAndWaitExample {
  
  val lock = new ReentrantLock()
  val TOTAL = 100 
  var counter = 0
  
  def main(args: Array[String]): Unit = {
    
    // A thread pool with 4 threads
    val pool = new ThreadPool(4)
    
    def runIncrements(): Unit = for (i <- 1 to TOTAL) {
      pool.async(_ => {
        lock.lock()
        counter += 1
        lock.unlock()
      })
    }
    
    println(s"About to run $TOTAL increments via the thread pool")
    
    try {
      pool.startAndWait(_ => runIncrements())
    } finally {
      // Let's not forget to clean up by shutting down all the threads
      pool.shutdown()
      }
    // Check the result

    assert(counter == TOTAL)
    println(s"All good!")
  } 

}
