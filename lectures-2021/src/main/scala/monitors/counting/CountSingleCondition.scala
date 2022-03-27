package monitors.counting

import java.util.concurrent.locks.ReentrantLock

import monitors.Logging
import util.ThreadID

/**
  * Example 2: Coditional variables
  * 
  * @author Ilya Sergey
  */
object CountSingleCondition extends Logging  {


  private var counter: Int = 0
  private val TOTAL = 100
  private val THREADS = 2


  private val lock = new ReentrantLock()
  private val cond = lock.newCondition()


  class AdderToEven extends Thread {
    override def run() = {
      val i = ThreadID.get
      for (_ <- 1 to TOTAL / THREADS) {
        // Step 1: take the lock
        lock.lock()
        try {
          // Step 2: wait while counter is not even
          while (counter % 2 == 1) {
            cond.await()
          }
          // Step 3: Do useful stuff
          counter = counter + 1
          println(s"Thread $i: now counter = $counter")
          // Step 4: Release the other thread 
          cond.signal()
        } finally {
          lock.unlock()
        }
      }
    }
  }
  
  /*
  TODO 1: What if we move `cond.signal()` before `counter = counter + 1`
  TODO 2: What if we remove `cond.signal()`    
  TODO 3: What happens if we call `cond.signal()` or `cond.await()` outside of the CR?
  TODO 4: What happens if replace `while (counter % 2 == 0)` by `if (counter % 2 == 0)`?     
   */

  class AdderToOdd extends Thread {
    override def run() = {
      val i = ThreadID.get
      for (_ <- 1 to TOTAL / 2) {
        lock.lock()
        try {
          while (counter % 2 == 0) {
            cond.await()
          }
          counter = counter + 1
          println(s"Thread $i: now counter = $counter")
          cond.signal()
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

    assert(counter == TOTAL)


    val formatter = java.text.NumberFormat.getIntegerInstance
    println()
    println(s"Total time: ${formatter.format(t2 - t1)} ms")

    

  }

}
