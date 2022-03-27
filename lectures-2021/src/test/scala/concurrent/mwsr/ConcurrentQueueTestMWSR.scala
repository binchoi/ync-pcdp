package concurrent.mwsr

import concurrent.ConcurrentQueue
import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
trait ConcurrentQueueTestMWSR extends FunSpec with Matchers {
  val THREADS = 2
  val input = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  def mkQueue(capacity: Int): ConcurrentQueue[Int]

  describe(s"A concurrent queue ${this.getClass.getName}") {
    it("should behave correctly") {
      val q = mkQueue(input.size)

      val t1 = new Enqueuer(input.take(5), q)
      val t2 = new Enqueuer(input.drop(5), q)

      // Start all threads
      t1.start()
      t2.start()

      // Wait for all threads to join
      t1.join()
      t2.join()

      val result = q.toListUnsafe
      println(result)
      assert(result.toSet == input.toSet)
    }
  }


  class Enqueuer(elems: List[Int], queue: ConcurrentQueue[Int]) extends Thread {
    override def run(): Unit = {
      for (e <- elems) {
        Thread.sleep(0, 2)
        queue.enq(e)
      }
    }
  }

}