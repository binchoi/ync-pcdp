package mutex

import java.util.concurrent.locks.Lock

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * @author Ilya Sergey
  */
trait GenericLockTest extends FunSpec with Matchers {
  val THREADS = 2
  val COUNT = 1024
  def PER_THREAD = COUNT / THREADS
  var counter: Int = 0

  def makeLockInstance: Lock
  val lockInstance = makeLockInstance

  describe(s"Two parallel threads with ${lockInstance.getClass.getName}") {
    it("should be mutually exclusive") {
      ThreadID.reset()
      val thread = Array.fill(THREADS)(new MyThread(lockInstance))

      // Start all threads
      for (i <- 0 until THREADS) {
        thread(i).start()
      }

      // Wait for all threads to join
      for (i <- 0 until THREADS) {
        thread(i).join()
      }

      assert(counter == COUNT)
    }
  }

  class MyThread(lockInstance: Lock) extends Thread {
    override def run(): Unit = {
      for (i <- 0 until PER_THREAD) {
        lockInstance.lock()
        try {
          val tmp = counter
          // println(s"Thread ${Thread.currentThread().getId}: $tmp")
          counter = tmp + 1
        }  finally {
          lockInstance.unlock()
        }
      }
    }
  }

}