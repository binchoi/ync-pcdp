package concurrent

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class IQueueTest extends FunSpec with Matchers {

  describe("The concurrent queue") {
    it("should exhibit FIFO properties with multiple threads") {

      val n = 10
      val q = new IQueue[Int]
      val input1 = (1 to n).toList
      val input2 = (n + 1 to 2 * n).toList
      
      val e1 = new Enqueuer(q, input1)
      val e2 = new Enqueuer(q, input2)
      val d1 = new Dequeuer(q, n)
      val d2 = new Dequeuer(q, n)

      e1.start()
      e2.start()
      d1.start()
      d2.start()

      e1.join()
      e2.join()

      d1.join()
      d2.join()
      
      val result1 = d1.accumulated
      val result2 = d2.accumulated
      
      assert(result1.forall(_.isDefined))
      assert(result2.forall(_.isDefined))
      
      val out1 = result1.map(_.get)
      val out2 = result2.map(_.get)
      
      val totalInput = input1
      val proj11 = out1.filter(input1.contains(_))
      val proj12 = out1.filter(input2.contains(_))
      val proj21 = out2.filter(input1.contains(_))
      val proj22 = out2.filter(input2.contains(_))
      
      assert(isFIFO(proj11))
      assert(isFIFO(proj12))
      assert(isFIFO(proj21))
      assert(isFIFO(proj22))

      
      assert((out1 ++ out2).toSet == (input1 ++ input2).toSet)
      
      // Hmm, these tests pass just fine

      // TODO: What else can we check about the queue (maybe not via just testing)?
    }
  }
  
  def isFIFO(l: List[Int]) = l == l.sorted

  class Enqueuer(val stack: IQueue[Int], items: List[Int]) extends Thread {
    override def run() = {
      for (e <- items) {
        stack.enq(e)
      }
    }
  }

  class Dequeuer(val queue: IQueue[Int], val iterations: Int) extends Thread {
    private var myAccumulator: List[Option[Int]] = Nil

    def accumulated: List[Option[Int]] = myAccumulator

    override def run() = {
      var i = 0
      while (i < iterations) {
        queue.deq() match {
          case None => // do nothing
          case s@Some(_) => 
            myAccumulator = myAccumulator ++ List(s)
            i = i + 1
        }
      }
    }
  }

}
