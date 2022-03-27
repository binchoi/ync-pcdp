package monitors.counting

import monitors.Logging
import spinlocks.TTASLock
import util.ThreadID

/**
  * Example 1: cooperative concurrency
  *
  * @author Ilya Sergey
  */
object CountTTASLock extends Logging {


  private var counter: Int = 0
  private val TOTAL = 100
  private val THREADS = 2


  private val lock = new TTASLock()


  class AdderToEven extends Thread {
    override def run() = {
      val i = ThreadID.get
      var k = 0
      while (k < TOTAL / THREADS) {
        // Step 1: take the lock
        lock.lock()
        try {
          // Step 2: only do stuff if some condition holds, otherwise yield
          if (counter % 2 == 0) {
            // Step 3: Do useful stuff
            counter = counter + 1
            println(s"Thread $i: now counter = $counter")
            k = k + 1
          }
        } finally {
          lock.unlock()
        }
      }
    }
  }

  class AdderToOdd extends Thread {
    override def run() = {
      val i = ThreadID.get
      var k = 0
      while (k < TOTAL / THREADS) {
        // Step 1: take the lock
        lock.lock()
        try {
          // Step 2: wait while counter is even
          if (counter % 2 == 1) {
            // Step 3: Do useful stuff
            counter = counter + 1
            println(s"Thread $i: now counter = $counter")
            k = k + 1
          }
        } finally {
          lock.unlock()
        }
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

    val formatter = java.text.NumberFormat.getIntegerInstance
    assert(counter == TOTAL)

    println()
    println(s"Total time:          ${formatter.format(t2 - t1)} ms")
    
  }
}
