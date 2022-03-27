package monitors.counting

import java.util.concurrent.locks.ReentrantLock

import monitors.Logging
import util.ThreadID

/**
  * Example 3: Multiple conditions for a single CR
  *
  * @author Ilya Sergey
  */

object CountMultipleConditions extends Logging {


  private var counter: Int = 0
  private val TOTAL = 100
  private val THREADS = 2
  
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
          println(s"Thread $i: now counter = $counter")
          // Step 4: Release the other thread
          condOdd.signal()
        }
      } finally {

        // Step 5: Release the lock 
        lock.unlock()
      }
    }
  }


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
          println(s"Thread $i: now counter = $counter")
          condEven.signal()
        }
      } finally {
        lock.unlock()
      }
    }
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)


    val evenAdder = new AdderToEven
    val oddAdder = new AdderToOdd

    val t1 = System.currentTimeMillis()
    evenAdder.start()
    oddAdder.start()

    evenAdder.join()
    oddAdder.join()
    val t2 = System.currentTimeMillis()

    assert(counter == TOTAL)


    val formatter = java.text.NumberFormat.getIntegerInstance
    println()
    println(s"Total time: ${formatter.format(t2 - t1)} ms")


  }

}
