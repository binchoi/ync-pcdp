package monitors.counting

import monitors.Logging
import util.ThreadID

/**
  * Example 5: Intrinsic Java monitors
  * 
  * @author Ilya Sergey
  */
object CountIntrinsicMonitor extends Logging {

  private var counter: Int = 0
  private val TOTAL = 100
  private val THREADS = 10

  class AdderToEven extends Thread {
    override def run() = {
      val i = ThreadID.get
      // Step 1: take the lock
      CountIntrinsicMonitor.synchronized {
        // Repeat in cycle
        for (_ <- 1 to TOTAL / THREADS) {

          // Step 2: wait while counter is not even
          while (counter % 2 == 1) {
            // TODO: Notice: now it's `wait()` instead of `await()`
            CountIntrinsicMonitor.wait()
          }
          // Step 3: Do useful stuff
          counter = counter + 1
          println(s"Thread $i (EvenAdder): now counter = $counter")
          
          // Step 4: Notify all other threads
          // TODO: Notice: now it's `notifyAll()` instead of `signalAll()`
          CountIntrinsicMonitor.notifyAll()
        }
      }
    }
  }
  
  // TODO 1: Why couldn't `use this.synchronized` instead?
  // TODO 2: Will it sill work if we replace `notifyAll()` by `notify()`?

  class AdderToOdd extends Thread {
    override def run() = {
      val i = ThreadID.get
      CountIntrinsicMonitor.synchronized {
        for (_ <- 1 to TOTAL / THREADS) {
          while (counter % 2 == 0) {
            CountIntrinsicMonitor.wait()
          }
          counter = counter + 1
          println(s"Thread $i (OddAdder) : now counter = $counter")
          CountIntrinsicMonitor.notifyAll()
        }
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
