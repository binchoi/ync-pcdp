package queues

import concurrent.{ConcurrentQueue, EmptyException}
import monitors.counting.CountManyThreads.println
import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
trait ConcurrentQueueTests extends FunSpec with Matchers {
  val CHUNK_SIZE = 1500

  def genChunk(i: Int) = ((i - 1) * CHUNK_SIZE + 1 to i * CHUNK_SIZE).toList

  def mkQueue(capacity: Int): ConcurrentQueue[Int]

  describe(s"A concurrent queue ${this.getClass.getName}") {
    it("should handle multiple enqueuers") {
      val q = mkQueue(CHUNK_SIZE * 3 + 1)

      val input1 = genChunk(1)
      val input2 = genChunk(2)
      val input3 = genChunk(3)
      val t1 = new Enqueuer(input1, q)
      val t2 = new Enqueuer(input2, q)
      val t3 = new Enqueuer(input3, q)
      val input = input1 ++ input2 ++ input3

      // TODO: Start all threads
      t1.start()
      t2.start()
      t3.start()

      // TODO: Wait for all threads to join
      t1.join()
      t2.join()
      t3.join()
      
      val result = q.toListThreadSafe
      
      // TODO: What can we assert about the result wrt. input?
      assert(input.size == result.size)
      assert(input.sorted == result.sorted)
    }
  }

  describe(s"A concurrent queue ${this.getClass.getName}") {
    it("should handle a single enqueuer and single dequeuer") {
      val q = mkQueue(CHUNK_SIZE / 10)

      val input = genChunk(1)
      val t1 = new Enqueuer(input, q)
      val t2 = new Dequeuer(q, input.size)

      // TODO: Here, we can check something stronger than in the test above.
      // TODO: Start both threads
      t1.start()
      t2.start()
      // TODO: Wait for all threads to join
      t1.join()
      t2.join()

      val result = t2.getResult
      
      assert(q.toListThreadSafe.isEmpty)
      assert(input == result)
    }
  }


  describe(s"A concurrent queue ${this.getClass.getName}") {
    it("should handle a many enqueuers and many dequeuers in a correct way") {
      val q = mkQueue(CHUNK_SIZE / 10)

      val input1 = genChunk(1)
      val input2 = genChunk(2)
      val e1 = new Enqueuer(input1, q)
      val e2 = new Enqueuer(input2, q)
      val d1 = new Dequeuer(q, input1.size)
      val d2 = new Dequeuer(q, input2.size)
      // TODO: Start enqueuers and dequeuers at the same time
      e1.start()
      d1.start()
      e2.start()
      d2.start()
      // TODO: Wait for all threads to join
      e1.join()
      e2.join()
      d1.join()
      d2.join()

      // TODO: Get what has been dequeued by the deuquers as result1 and result2.
      val result1 = d1.getResult
      val result2 = d2.getResult
      // TODO: What about the whole set of elements?
      assert(input1 ++ input2 == (result1 ++ result2).sorted)

      def orderPreserved(input: List[Int], result: List[Int]): Boolean = {
        // Taking advantage of input being sorted
        val projection = result.filter(input.contains(_))
        projection.sorted == projection
      }
      // TODO: What about result1 and result2? Try to make use of the helper function above.
      assert(orderPreserved(input1, result1))
      assert(orderPreserved(input1, result2))
      assert(orderPreserved(input2, result1))
      assert(orderPreserved(input2, result2))
    }
  }



  class Enqueuer(elems: List[Int], queue: ConcurrentQueue[Int]) extends Thread {
    override def run(): Unit = {
      for (e <- elems) {
        queue.enq(e)
        // println(s"Enqueued $e")
      }
    }
  }

  class Dequeuer(queue: ConcurrentQueue[Int], howMany: Int) extends Thread {

    private var result: List[Int] = Nil

    def getResult = result.reverse

    override def run(): Unit = {

      var i = 0
      while (i < howMany) {
        try {
          val r = queue.deq()
          // println(s"Dequeued $r")
          result = r :: result
          i += 1
        } catch {
          case EmptyException =>
          // just skip and keep spinning
        }
      }
    }
  }

}
