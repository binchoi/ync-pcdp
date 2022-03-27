package blocking

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class CountBlockTests extends FunSpec with Matchers {

  // Simple atomic (linearisable) counter
  val counter = new AtomicInteger(0)

  // A number of threads to compete.
  // Feel free to change for your experiments.
  private val NUM_THREADS = 4

  ///////////////////////////////////////////////////////////////
  // Provided tests
  ///////////////////////////////////////////////////////////////

  describe(s"Concurrent threads synchronising using CountBlock") {
    it("should proceed synchronously") {
      ThreadID.reset()
      val stepOneBlocker = new CountBlock(4)
      val stepTwoBlocker = new CountBlock(4)

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

    }
  }

  describe(s"should behave correctly when used with various data structures [QUEUE]") {
    // When numThread of CountBlock = 1, it should behave like similar to basic monitors
    // Test adapted from Prof Sergey's MonitorQueue example

    import java.util.concurrent.ArrayBlockingQueue
    val NUM_E_THREADS, NUM_D_THREADS = 2

    val myQ = new ArrayBlockingQueue[Int](1000)

    val halfTimeEBlocker = new CountBlock(NUM_E_THREADS)
    val fullTimeEBlocker = new CountBlock(NUM_E_THREADS)

    class Enqueuer(elems: List[Int]) extends Thread {
      override def run() {
        val half : Int = elems.size / 2
        val fstHalf = elems.take(half)
        val sndHalf = elems.drop(half)
        // enq first half of the elements given to the enqueuer to enq
        for (e <- fstHalf) {
          myQ.add(e) // this is mutex
        }
        halfTimeEBlocker.countDown()
        // Now all enqueuers wait for the (awaken) main thread
        // to count down for them to proceed further
        fullTimeEBlocker.await()

        for (e <- sndHalf) {
          myQ.add(e)
        }
      }
    }

    val halfTimeDBlocker = new CountBlock(NUM_D_THREADS)
    val fullTimeDBlocker = new CountBlock(NUM_D_THREADS)

    class Dequeuer(size: Int) extends Thread {
      override def run() {
        // deq half of the total number of deq's it will execute
        for (e <- 1 to size/2) {
          myQ.poll() // this is mutex
        }
        halfTimeDBlocker.countDown()
        // Now all dequeuers wait for the (awaken) main thread
        // to count down for them to proceed further
        fullTimeDBlocker.await()

        for (e <- 1 to size/2) {
          myQ.poll()
        }
      }
    }

    it("should behave correctly when multiple threads ENQ and DEQ") {
      val input = (1 to 1000).toList
      val half = input.size / 2

      // Lazily fill a list of threads
      val threadsE: List[Enqueuer] = List(new Enqueuer(input.take(half)), new Enqueuer(input.drop(half)))
      val threadsD: List[Dequeuer] = List(new Dequeuer(input.size/2), new Dequeuer(input.size / 2))

      // Start all threads
      threadsE.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeEBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_E_THREADS).toList.foreach(_ => fullTimeEBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsE.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 1000)

      ///////////////////////DEQUEUE TIME///////////////////////////////
      threadsD.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeDBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_D_THREADS).toList.foreach(_ => fullTimeDBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsD.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 0)
      assert(myQ.isEmpty)
    }

    it("should behave correctly when more threads ENQ and DEQ concurrently") {
      val NUM_E_THREADS, NUM_D_THREADS = 10

      val myQ = new ArrayBlockingQueue[Int](1000)

      val halfTimeEBlocker = new CountBlock(NUM_E_THREADS)
      val fullTimeEBlocker = new CountBlock(NUM_E_THREADS)

      class Enqueuer(elems: List[Int]) extends Thread {
        override def run() {
          val half : Int = elems.size / 2
          val fstHalf = elems.take(half)
          val sndHalf = elems.drop(half)
          // enq first half of the elements given to the enqueuer to enq
          for (e <- fstHalf) {
            myQ.add(e) // this is mutex
//            println(s"enq $e")
          }
          halfTimeEBlocker.countDown()
          // Now all enqueuers wait for the (awaken) main thread
          // to count down for them to proceed further
          fullTimeEBlocker.await()

          for (e <- sndHalf) {
            myQ.add(e)
          }
        }
      }

      val halfTimeDBlocker = new CountBlock(NUM_D_THREADS)
      val fullTimeDBlocker = new CountBlock(NUM_D_THREADS)

      class Dequeuer(size: Int) extends Thread {
        override def run() {
          // deq half of the total number of deq's it will execute
          for (e <- 1 to size/2) {
            myQ.poll() // this is mutex
          }
          halfTimeDBlocker.countDown()
          // Now all dequeuers wait for the (awaken) main thread
          // to count down for them to proceed further
          fullTimeDBlocker.await()

          for (e <- 1 to size/2) {
            myQ.poll()
          }
        }
      }

      val input = (1 to 1000).toList

      val ranges = input.grouped(input.size / NUM_E_THREADS).toList
      var threadsE: List[Enqueuer] = List()
      val sizePerD = input.size / NUM_D_THREADS
      for (r <- ranges) {
        threadsE = threadsE ++ List(new Enqueuer(r))
      }
      var threadsD: List[Dequeuer] = List.fill(NUM_D_THREADS)(new Dequeuer(sizePerD))

      // Start all threads
      threadsE.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeEBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_E_THREADS).toList.foreach(_ => fullTimeEBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsE.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 1000)

      ///////////////////////DEQUEUE TIME///////////////////////////////
      threadsD.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeDBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_D_THREADS).toList.foreach(_ => fullTimeDBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsD.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 0)
      assert(myQ.isEmpty)
    }

    it("should behave correctly when MORE threads [Queue]") {
      ThreadID.reset()

      val NUM_E_THREADS, NUM_D_THREADS = 25

      val myQ = new ArrayBlockingQueue[Int](1000)

      val halfTimeEBlocker = new CountBlock(NUM_E_THREADS)
      val fullTimeEBlocker = new CountBlock(NUM_E_THREADS)

      class Enqueuer(elems: List[Int]) extends Thread {
        override def run() {
          val half : Int = elems.size / 2
          val fstHalf = elems.take(half)
          val sndHalf = elems.drop(half)
          // enq first half of the elements given to the enqueuer to enq
          for (e <- fstHalf) {
            myQ.add(e) // this is mutex
            //            println(s"enq $e")
          }
          halfTimeEBlocker.countDown()
          // Now all enqueuers wait for the (awaken) main thread
          // to count down for them to proceed further
          fullTimeEBlocker.await()

          for (e <- sndHalf) {
            myQ.add(e)
          }
        }
      }

      val halfTimeDBlocker = new CountBlock(NUM_D_THREADS)
      val fullTimeDBlocker = new CountBlock(NUM_D_THREADS)

      class Dequeuer(size: Int) extends Thread {
        override def run() {
          // deq half of the total number of deq's it will execute
          for (e <- 1 to size/2) {
            myQ.poll() // this is mutex
          }
          halfTimeDBlocker.countDown()
          // Now all dequeuers wait for the (awaken) main thread
          // to count down for them to proceed further
          fullTimeDBlocker.await()

          for (e <- 1 to size/2) {
            myQ.poll()
          }
        }
      }

      val input = (1 to 1000).toList

      val ranges = input.grouped(input.size / NUM_E_THREADS).toList
      var threadsE: List[Enqueuer] = List()
      val sizePerD = input.size / NUM_D_THREADS
      for (r <- ranges) {
        threadsE = threadsE ++ List(new Enqueuer(r))
      }
      var threadsD: List[Dequeuer] = List.fill(NUM_D_THREADS)(new Dequeuer(sizePerD))

      // Start all threads
      threadsE.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeEBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_E_THREADS).toList.foreach(_ => fullTimeEBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsE.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 1000)

      ///////////////////////DEQUEUE TIME///////////////////////////////
      threadsD.foreach(_.start()) // start all enqueuer threads

      // Make sure they all reach this point
      // This mimics functionality similar to Thread.join()
      halfTimeDBlocker.await() // main thread is awaiting at halfTime; all other threads are awaiting fullTime

      // All threads completed half of all enq's and waiting
      assert(myQ.size() == 1000/2)

      1.to(NUM_D_THREADS).toList.foreach(_ => fullTimeDBlocker.countDown())

      // Only now make sure that the threads terminate
      threadsD.foreach(_.join())

      // All threads completed step two
      assert(myQ.size() == 0)
      assert(myQ.isEmpty)
    }
  }


  describe(s"A deeper investigation of the CountBlock ") {
    it("should proceed synchronously and produce a correct history") {
      val counter = new AtomicInteger(0)
      val isBelowTwenty = new AtomicBoolean(false)
      val belowTwenty = new CountBlock(1)
      val aboveNineteen = new CountBlock(1)
      var history: List[(Int, Boolean)] = List()

      ThreadID.reset()

      class belowTwentyWorker extends Thread {
        override def run() {
          while (counter.get() < 50) {
            while (counter.get() > 19) {
              isBelowTwenty.set(false)
              aboveNineteen.countDown()
              belowTwenty.await()

              if (counter.get() >49) { // To finish the execution of threads in the waiting room
                return None
              }
            }
            isBelowTwenty.set(true)
            counter.getAndIncrement()
//            println(List((counter.get(), isBelowTwenty.get())))
            history = history ++ List((counter.get(), isBelowTwenty.get()))
          }
        }
      }

      class aboveNineteenWorker extends Thread {
        override def run() {
          while (counter.get < 50) {
            while (counter.get() < 20) {
              aboveNineteen.await()
            }
            counter.getAndIncrement()
//            println(List((counter.get(), isBelowTwenty.get())))
            history = history ++ List((counter.get(), isBelowTwenty.get()))
          }
          belowTwenty.countDown() // to finish the execution of the thread process(es) in belowTwenty waiting room
        }
      }

      // Lazily fill a list of threads
      val threads = List(new aboveNineteenWorker, new belowTwentyWorker)

      // Start all threads
      threads.foreach(_.start()) // start all worker threads

      threads.foreach(_.join())

      assert(history == (List((1,true), (2,true), (3,true), (4,true), (5,true), (6,true),
        (7,true), (8,true), (9,true), (10,true), (11,true), (12,true), (13,true), (14,true),
        (15,true), (16,true), (17,true), (18,true), (19,true), (20,true), (21,false),
        (22,false), (23,false), (24,false), (25,false), (26,false), (27,false), (28,false),
        (29,false), (30,false), (31,false), (32,false), (33,false), (34,false), (35,false),
        (36,false), (37,false), (38,false), (39,false), (40,false), (41,false), (42,false),
        (43,false), (44,false), (45,false), (46,false), (47,false), (48,false), (49,false),
        (50,false))))

    }
  }
}





