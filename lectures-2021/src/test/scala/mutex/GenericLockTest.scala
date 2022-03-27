package mutex

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

import java.util.concurrent.locks.Lock

/**
  * @author Ilya Sergey
  */
trait GenericLockTest extends FunSpec with Matchers {
  val THREADS = 2
  val COUNT = 128
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

      // TODO: Add the condition!
      assert(counter == COUNT, s"The result of counter should be $COUNT")
    }
  }

  class MyThread(lockInstance: Lock) extends Thread {
    override def run(): Unit = {
      for (i <- 0 until PER_THREAD) {
        lockInstance.lock()
        try {
          val c = counter
          counter = c + 1
          // println(s"Thread ${ThreadID.get}, counter = $c")
        } finally {
          lockInstance.unlock()
        }
      }
    }
  }

}