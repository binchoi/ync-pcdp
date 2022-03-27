package spinlocks

import java.util.concurrent.locks.Lock

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * @author Ilya Sergey
  */
trait SpinLockTest extends FunSpec with Matchers {

  val THREADS = 4
  val COUNT = 10000000

  def PER_THREAD = COUNT / THREADS

  var counter: Int = 0

  def makeSpinLockInstance: Lock

  val lockInstance = makeSpinLockInstance

  describe(s"$THREADS parallel threads with ${lockInstance.getClass.getName}") {
    it("should be mutually exclusive") {
      ThreadID.reset()
      val thread = Array.fill(THREADS)(new MyThread(lockInstance))


      val t1 = System.currentTimeMillis()

      // Start all threads
      for (i <- 0 until THREADS) {
        thread(i).start()
      }

      // Wait for all threads to join
      for (i <- 0 until THREADS) {
        thread(i).join()
      }

      val t2 = System.currentTimeMillis()

      assert(counter == COUNT, s"The result of counter should be $COUNT")


      val formatter = java.text.NumberFormat.getIntegerInstance

      //      val spinning = thread.map(_.timeSpun).sum / 1000000
      //      val total = thread.map(_.timeSpend).sum / 1000000

      println()
      println(s"Statistics for ${lockInstance.getClass.getName}:")
      println(s"Number of threads:   ${THREADS}")
      println(s"Total time:          ${formatter.format(t2 - t1)} ms")
      //      println(s"Total time working:  ${formatter.format(total - spinning)} ms")
      //      println(s"Total time spinning: ${formatter.format(spinning)} ms")
      //      println(s"Spinning/Total:      ${f"${spinning.toDouble/total.toDouble}%1.2f"}")

    }
  }


  class MyThread(lockInstance: Lock) extends Thread {

    private var timeSpentSpinning: Long = 0
    private var timeSpent: Long = 0

    def timeSpun: Long = timeSpentSpinning

    def timeSpend: Long = timeSpent

    override def run(): Unit = {
      for (i <- 0 until PER_THREAD) {
        //val t1 = System.nanoTime()
        lockInstance.lock()
        //val t2 = System.nanoTime()
        try {
          counter = counter + 1
        } finally {
          //val t3 = System.nanoTime()
          lockInstance.unlock()
          //val t4 = System.nanoTime()
          //timeSpentSpinning += (t2 - t1) + (t4 - t3)
          //timeSpent += (t4 - t1)
        }
      }
    }
  }


}
