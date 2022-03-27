package monitors

import monitors.queue.MonitorQueue
import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class MonitorQueueTest extends FunSpec with Matchers {
  val THREADS = 2
  val input = (1 to 1000).toList
  val half = input.size / 2

  def mkQueue(capacity: Int) = new MonitorQueue[Int](10)

  describe(s"A monitor-based queue ${this.getClass.getName}") {
    it("should behave correctly") {
      val q = mkQueue(input.size)

      val e1 = new Enqueuer(input.take(half), q)
      val e2 = new Enqueuer(input.drop(half), q)
      val d1 = new Dequeuer(q)
      val d2 = new Dequeuer(q)

      // Start all threads
      e1.start()
      d1.start()
      e2.start()
      d2.start()

      // Wait for all threads to join
      e1.join()
      e2.join()
      d1.join()
      d2.join()

      val result = d1.accumulated ++ d2.accumulated
      //println(result)
      assert(result.toSet == input.toSet)
    }
  }


  class Enqueuer(elems: List[Int], queue: MonitorQueue[Int]) extends Thread {
    override def run(): Unit = {
      for (e <- elems) {
        Thread.sleep(0, 2)
        queue.enq(e)
        // println(s"Enqueued $e")
      }
    }
  }

  class Dequeuer(queue: MonitorQueue[Int]) extends Thread {
    private var acc: List[Int] = Nil
    
    def accumulated = acc

    override def run(): Unit = {
      for (i <- 1 to half) {
        val e = queue.deq()
        acc = acc :+ e
        //println(s"Dequeued $e")
      }
    }
  }

}