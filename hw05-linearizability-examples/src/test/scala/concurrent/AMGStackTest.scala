package concurrent

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class AMGStackTest extends FunSpec with Matchers {

  describe("The concurrent stack") {
    it("should exhibit LIFO properties with multiple threads") {

      val n = 10
      val s = new AMGStack[Int]
      val input1 = (1 to n).toList
      val input2 = (n + 1 to 2 * n).toList
      
      val e1 = new Pusher(s, input1)
      val e2 = new Pusher(s, input2)
      val d = new Popper(s, 2 * n)
      e1.start()
      e2.start()
      d.start()

      e1.join()
      e2.join()
      d.join()
      
      val result1 = d.accumulated
      
      assert(result1.forall(_.isDefined))
      
      val out = result1.map(_.get)
      val totalInput = input1 ++ input2
      assert(totalInput.toSet == out.toSet)
      
      //TODO: What else can we assert or observe?
    }
  }
  
  class Pusher(val stack: AMGStack[Int], items: List[Int]) extends Thread {
    override def run() = {
      for (e <- items) {
        stack.push(e)
      }
    }
  }

  class Popper(val stack: AMGStack[Int], val iterations: Int) extends Thread {
    private var myAccumulator: List[Option[Int]] = Nil

    def accumulated: List[Option[Int]] = myAccumulator

    override def run() = {
      var i = 0
      while (i < iterations) {
        stack.pop() match {
          case None =>
          case x => 
            myAccumulator = myAccumulator ++ List(x)
            i = i + 1
        }
        
      }
    }
  }

}
