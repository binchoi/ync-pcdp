package mutex

import counting.Counter
import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * @author Ilya Sergey
  */
class TreeLockTests extends FunSpec with Matchers {

  // A number of threads to compete.
  // Feel free to change for your experiments.
  private val NUM_THREADS = 10

  describe(s"Concurrent threads using TreeLock") {
    it("should be mutually exclusive when incrementing") {
      ThreadID.reset()

      // Shared counter to be used by multiple threads
      val ctr = new Counter(NUM_THREADS, totalCount = 1000000)

      // Creating an array of threads all working with the same
      // counter-incrementing Runnable
      // check Counter's `run()` method
      val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

      // Start all threads
      for (i <- 0 until NUM_THREADS) {
        thread(i).start()
      }

      // Wait for all threads to join
      try {
        for (i <- 0 until NUM_THREADS) {
          thread(i).join()
        }
      } catch {
        case ex: InterruptedException =>
          System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
      }

      assert(ctr.totalCount == ctr.getCount,
        s"The result of counter should be ${ctr.totalCount}}")
    }
    it(s"should be able to function with different number of threads") {
      // Note that because `timesThrIncs' of Counter class uses floor division, only thread numbers that divide
      // 1000000 without remainder was used (else, there is a margin of error)
      for (NUM_THREADS <- Array(1, 2, 4, 5, 8, 20, 32)) {
        ThreadID.reset()

        // Shared counter to be used by multiple threads
        val ctr = new Counter(NUM_THREADS, totalCount = 1000000)

        // Creating an array of threads all working with the same
        // counter-incrementing Runnable
        // check Counter's `run()` method
        val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

        // Start all threads
        for (i <- 0 until NUM_THREADS) {
          thread(i).start()
        }

        // Wait for all threads to join
        try {
          for (i <- 0 until NUM_THREADS) {
            thread(i).join()
          }
        } catch {
          case ex: InterruptedException =>
            System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
        }

        assert(ctr.totalCount == ctr.getCount,
          s"The result of counter should be ${ctr.totalCount}}")
      }
    }

    it(s"should be able to function with longer program") {
      // Note that because `timesThrIncs' of Counter class uses floor division, only thread numbers that divide
      // 3000000 without remainder was used (else, there is a margin of error)
      for (NUM_THREADS <- Array(1, 2, 8)) {
        ThreadID.reset()

        // Shared counter to be used by multiple threads
        val ctr = new Counter(NUM_THREADS, totalCount = 3000000) // Wow that's a some stress-testing!

        // Creating an array of threads all working with the same
        // counter-incrementing Runnable
        // check Counter's `run()` method
        val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

        // Start all threads
        for (i <- 0 until NUM_THREADS) {
          thread(i).start()
        }

        // Wait for all threads to join
        try {
          for (i <- 0 until NUM_THREADS) {
            thread(i).join()
          }
        } catch {
          case ex: InterruptedException =>
            System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
        }

        assert(ctr.totalCount == ctr.getCount,
          s"The result of counter should be ${ctr.totalCount}}")
      }
    }

    it(s"should be functional with different number of threads involved/executing") {
      // Note that because `timesThrIncs' of Counter class uses floor division, only thread numbers that divide
      // 1000000 without remainder was used (else, there is a margin of error)
      for (NUM_THREADS <- Array(1, 2, 4, 5, 8, 20)) {
        ThreadID.reset()

        // Shared counter to be used by multiple threads
        val ctr = new Counter(NUM_THREADS, totalCount = 1000000)

        // Creating an array of threads all working with the same
        // counter-incrementing Runnable
        // check Counter's `run()` method
        val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

        // Start all threads
        for (i <- 0 until NUM_THREADS) {
          thread(i).start()
        }

        // Wait for all threads to join
        try {
          for (i <- 0 until NUM_THREADS) {
            thread(i).join()
          }
        } catch {
          case ex: InterruptedException =>
            System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
        }

        assert(ctr.totalCount == ctr.getCount,
          s"The result of counter should be ${ctr.totalCount}}")
      }
    }
  }

  // Let's test if TreeLock can be used with various concurrent data structures
  describe("The concurrent queue") {
    it("should exhibit FIFO properties when using TreeLock") {
      val NUM_THREADS = 2

      ThreadID.reset()
      val myTreeLock = new TreeLock(2)
      var myQ = new ConcurrentQueue[Int](myTreeLock)

      var enq_order = List()
      var deq_order = List()

      val ctr = new EnqDeq(myQ, enq_order, deq_order)

      val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

      // Start all threads
      for (i <- 0 until NUM_THREADS) {
        thread(i).start()
      }

      // Wait for all threads to join
      try {
        for (i <- 0 until NUM_THREADS) {
          thread(i).join()
        }
      } catch {
        case ex: InterruptedException =>
          System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
      }

      assert(enq_order == deq_order) // enq_order and deq_order is the same
    }

    it("should not create spurious elements in the presence of concurrency") {
      val NUM_THREADS = 2

      ThreadID.reset()
      val myTreeLock = new TreeLock(2)
      var myQ = new ConcurrentQueue[Int](myTreeLock)

      var enq_order = List()
      var deq_order = List()

      val ctr = new EnqDeq(myQ, enq_order, deq_order)

      val thread = Array.fill(NUM_THREADS)(new Thread(ctr))

      // Start all threads
      for (i <- 0 until NUM_THREADS) {
        thread(i).start()
      }

      // Wait for all threads to join
      try {
        for (i <- 0 until NUM_THREADS) {
          thread(i).join()
        }
      } catch {
        case ex: InterruptedException =>
          System.out.println(s"Something went wrong during joining:\n${ex.toString}}")
      }

      assert(enq_order sameElements deq_order) // same elements that were enqueued are dequeued. No spurious elements
    }
  }


  class EnqDeq(val queue: ConcurrentQueue[Int], var nq_order: List[Int], var dq_order: List[Int]) extends Thread {

    override def run() = {
      for (i <- 1 to 10) {
        queue.enq(i)
        nq_order = i :: nq_order
      }

      for (i <- 1 to 10) {
        val result = queue.deq
        result match {
          case Some(value: Int) =>
            dq_order = value :: dq_order
          case None =>
        }
      }
    }
  }
}

