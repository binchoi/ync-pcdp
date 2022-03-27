package concurrent.swsr

import concurrent.{EmptyException, ConcurrentQueue}
import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
 * @author Ilya Sergey
 */
trait ConcurrentQueueTestSWSR extends FunSpec with Matchers {
  val THREADS = 2
  val input = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  def mkQueue(capacity: Int): ConcurrentQueue[Int]

  describe(s"A concurrent queue ${this.getClass.getName}") {
    it("should behave correctly") {
      val q = mkQueue(input.size)

      val t1 = new Enqueuer(input, q)
      val t2 = new Dequeuer(q, input.size)

      // Start all threads
      t1.start()
      t2.start()

      // Wait for all threads to join
      t1.join()
      t2.join()

      val result = t2.getResult
      println(result)
      assert(result == input)
    }
  }

  class Enqueuer(elems: List[Int], queue: ConcurrentQueue[Int]) extends Thread {
    override def run(): Unit = {

      // TODO: Add all elements from `elems` to the queue
      for (e <- elems) {
        Thread.sleep(0, 1)
        queue.enq(e)
        //        println(s"Thread ${ThreadID.get}, enqueued $e")
      }
    }
  }

  class Dequeuer(queue: ConcurrentQueue[Int], iterations: Int) extends Thread {
    private var result: List[Int] = Nil

    def getResult = result

    override def run(): Unit = {
      // TODO: Dequeue as many elements as there are iterations
      var i: Int = 0
      while (i < iterations) {
        try {
          val x = queue.deq()
          //          println(s"Thread ${ThreadID.get}, dequeued $x")
          result = result :+ x
          i = i + 1
        } catch {
          case EmptyException => // do nothing
        }
      }

    }
  }


}
