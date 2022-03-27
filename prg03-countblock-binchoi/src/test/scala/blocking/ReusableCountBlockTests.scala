package blocking

import java.util.concurrent.atomic.AtomicInteger
import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * Please, provide tests with multiple stages to evaluate your implementation
  * of ReusableCountBlock
  */
class ReusableCountBlockTests extends FunSpec with Matchers {

  val counter = new AtomicInteger(0)

  private val NUM_THREADS = 4

  describe(s"Reusable CountBlock") {
    it("should allow for scenarios when block is reused") {
      // Design the tests that reuses the same block for 
      // repeating the similar scenario with multiple "stages"
      ThreadID.reset()
      val stepOneBlocker = new ReusableCountBlock(4)
      val stepTwoBlocker = new ReusableCountBlock(4)

      class Worker extends Thread {
        override def run() {
          counter.getAndIncrement() // this is mutex
          stepOneBlocker.countDown()
          // Now all workers wait for the (awaken) main thread
          // to count down for them to proceed further
          stepTwoBlocker.await()
          counter.getAndIncrement()
        }
      }

      // Lazily fill a list of threads
      val threads = List.fill(NUM_THREADS)(new Worker)

      // Start all threads
      threads.foreach(_.start()) // start all worker threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      stepOneBlocker.await() // main thread is awaiting at stepOne; all other threads are awaiting stepTwo

      // All threads completed step one and waiting
      assert(counter.get() == NUM_THREADS)

      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())

      // Only now make sure that the threads terminate
      threads.foreach(_.join())

      // All threads completed step two
      assert(counter.get() == NUM_THREADS * 2)

      stepOneBlocker.reset()
      stepTwoBlocker.reset()
      //////////////////////////// Let's use the same Blockers again for similar process! ////////////////////////
      class Worker2 extends Thread {
        override def run() {
          counter.getAndIncrement()
          counter.getAndIncrement()
          stepOneBlocker.countDown()
          // Now all workers wait for the (awaken) main thread
          // to count down for them to proceed further
          stepTwoBlocker.await()
          counter.getAndIncrement()
          counter.getAndIncrement()
        }
      }

      // Lazily fill a list of threads
      val threads2 = List.fill(NUM_THREADS)(new Worker2)
      // Start all threads
      threads2.foreach(_.start()) // start all worker threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      stepOneBlocker.await() // main thread is awaiting at stepOne; all other threads are awaiting stepTwo

      // All threads completed step one and waiting
      assert(counter.get() == NUM_THREADS * 4)

      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())

      // Only now make sure that the threads terminate
      threads.foreach(_.join())

      // All threads completed step two
      assert(counter.get() == NUM_THREADS * 6)
    }


    it("should block at countDown()") {
      // Adapted from given code below
      // The problem states that countblock is initialised with a number n which corresponds to how
      // many active threads that it is going to account for.

      // This does not mean that we will be given n active threads EVERYTIME or n countDown() method calls
      // before each reset. In the scenario where the number of threads that are originally active is greater than n,
      // we will block extra threads at countDown() such that only n threads (of the originally-active threads)
      // are active in subsequent method calls (we will add some thread.sleep to demonstrate this clearly)
      ThreadID.reset()
      counter.set(0)
      val stepOneBlocker = new ReusableCountBlock(NUM_THREADS)
      val stepTwoBlocker = new ReusableCountBlock(NUM_THREADS)

      class Worker extends Thread {
        override def run(): Unit = {
          counter.getAndIncrement()
          stepOneBlocker.countDown() // after this line extra threads will be filtered
          stepTwoBlocker.await()
          counter.getAndIncrement()
        }
      }

      val threads = List.fill(NUM_THREADS + 2)(new Worker)
      threads.foreach(_.start())
      Thread.sleep(100)
      stepOneBlocker.await()
      // initially count is above NUM_THREADS
      // due to the sufficient thread.sleep, we can accurately guess...
      assert(counter.get() == NUM_THREADS+2)
      // but in general...
      assert(counter.get() > NUM_THREADS)

      counter.set(0)

      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())
      Thread.sleep(100)
      // sleep to wait for threads to complete their execution
      // using the join command is not appropriate as one thread is BLOCKED (and in waiting room) - it will wait forever
      assert(counter.get() == NUM_THREADS) // as foretold, subsequent method calls show that only n of the originally
      // active threads are active

      stepOneBlocker.reset()
      stepTwoBlocker.reset()
      // the extra threads that were waiting in the waiting room are freed when reset

      counter.set(0)

      val threads1 = List.fill(NUM_THREADS*3)(new Worker)
      threads1.foreach(_.start())
      Thread.sleep(100)
      stepOneBlocker.await()
      assert(counter.get() > NUM_THREADS) // Again -- counter is higher than expected due to the many additional threads
      // but countDown() method call has sent all the (2*NUM_THREADS) extra threads to the waiting room,
      // leaving only (NUM_THREADS) originally-active threads still active in subsequent method calls
      counter.set(0)
      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())
      Thread.sleep(100)
      assert(counter.get() == NUM_THREADS) // Boom! Evidence that some threads have been BLOCKED at countDown()
    }
    
    ///////////////////////////////////////////////////////////////
    // Provided tests
    ///////////////////////////////////////////////////////////////

    it("should allow for staged scenarios") {
      ThreadID.reset()
      counter.set(0)
      val stepOneBlocker = new ReusableCountBlock(NUM_THREADS)
      val stepTwoBlocker = new ReusableCountBlock(NUM_THREADS)

      class Worker extends Thread {
        override def run(): Unit = {
          counter.getAndIncrement()
          stepOneBlocker.countDown()
          stepTwoBlocker.await()
          counter.getAndIncrement()
        }
      }

      val threads = List.fill(NUM_THREADS)(new Worker)
      threads.foreach(_.start())
      stepOneBlocker.await()
      assert(counter.get() == NUM_THREADS)
      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())
      threads.foreach(_.join())
      assert(counter.get() == NUM_THREADS * 2)

      stepOneBlocker.reset()
      stepTwoBlocker.reset()

      // Reuse same Count Block objects
      val threads1 = List.fill(NUM_THREADS)(new Worker)
      threads1.foreach(_.start())
      stepOneBlocker.await()
      assert(counter.get() == NUM_THREADS * 3)
      1.to(NUM_THREADS).toList.foreach(_ => stepTwoBlocker.countDown())
      threads1.foreach(_.join())
      assert(counter.get() == NUM_THREADS * 4)
    }
  }

}
