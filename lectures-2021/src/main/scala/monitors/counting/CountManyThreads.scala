package monitors.counting

import java.util.concurrent.locks.ReentrantLock

import monitors.Logging
import util.ThreadID

/**
  * Example 3: Multiple conditions for a single CR
  *
  * @author Ilya Sergey
  */

object CountManyThreads extends Logging {
  
  private var counter: Int = 0
  private val TOTAL = 100
  private val THREADS = 10

  private val lock = new ReentrantLock()
  private val condEven = lock.newCondition()
  private val condOdd = lock.newCondition()
  
  class AdderToEven extends Thread {
    override def run() = {
      val i = ThreadID.get
      // Step 1: take the lock
      lock.lock()
      try {

        // Repeat in cycle
        for (_ <- 1 to TOTAL / THREADS) {

          // Step 2: wait while counter is not even
          while (counter % 2 == 1) {
            condEven.await()
          }
          // Step 3: Do useful stuff
          counter = counter + 1
          println(s"Thread $i (EvenAdder): now counter = $counter")

          // Step 4: Signal some other threads
          condOdd.signal()
        }
      } finally {

        // Step 5: Release the lock 
        lock.unlock()
      }
    }
  }
  
  // TODO: Can we notify many threads (`signallAll()`)? What's the difference?


  class AdderToOdd extends Thread {
    override def run() = {
      val i = ThreadID.get
      lock.lock()
      try {
        for (_ <- 1 to TOTAL / THREADS) {
          while (counter % 2 == 0) {
            condOdd.await()
          }
          counter = counter + 1
          println(s"Thread $i (OddAdder) : now counter = $counter")
          condEven.signal()
        }
      } finally {
        lock.unlock()
      }
    }
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
    
    val evens = for (i <- 1 to 5) yield new AdderToEven
    val odds = for (i <- 1 to 5) yield new AdderToOdd

    val t1 = System.currentTimeMillis()
    
    for (t <- evens ++ odds) {
      t.start()
    }

    for (t <- evens ++ odds) {
      t.join()
    }
    
    val t2 = System.currentTimeMillis()

    assert(counter == TOTAL)


    val formatter = java.text.NumberFormat.getIntegerInstance
    println()
    println(s"Total time: ${formatter.format(t2 - t1)} ms")


  }

}
