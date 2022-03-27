package homework.problem4

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class Problem4Test extends FunSpec with Matchers {
  
  describe("A sequential queue") {
    it("must follow the FIFO order of enqueue/dequeue") {
      val q = new SequentialQueue[Int]
      // TODO: you might want to fill the queue now
      q.enq(1)
      q.enq(2)
      q.enq(3)
      q.enq(4)
      q.enq(5)
      q.enq(6)
      // TODO: implement me!
      assert(q.deq.contains(1))
      assert(q.deq.contains(2))
      assert(q.deq.contains(3))
      assert(q.deq.contains(4))
      assert(q.deq.contains(5))
      assert(q.deq.contains(6))
    }

    it("must have the size() method consistent with isEmpty") {
      val q = new SequentialQueue[Int]
      q.enq(1)
      assert(q.size == 1)
      q.deq
      // TODO: implement me!
      assert(q.isEmpty & q.size == 0)
    }

    it("should provide the semantics of deq() consistent with size() and isEmp()") {
      // TODO: implement me!
      val q = new SequentialQueue[Int]
      q.enq(1)
      assert(q.size>0 & !(q.isEmpty) & q.deq.contains(1)) // dequeue outputs Some(value)
      assert(q.isEmpty & q.size == 0) // queue is empty
      assert(q.deq.isEmpty) // The result of q.dequeue is None
    }
  }

  describe("A sequential stack") {
    it("must follow the LIFO order of push/pop") {
      val q = new SequentialStack[Int]
      q.push(1)
      q.push(2)
      q.push(3)
      q.push(4)
      q.push(5)
      q.push(6)
      // TODO: implement me!
      assert(q.pop.contains(6))
      assert(q.pop.contains(5))
      assert(q.pop.contains(4))
      assert(q.pop.contains(3))
      assert(q.pop.contains(2))
      assert(q.pop.contains(1))

    }

    it("must have the size() method consistent with isEmpty") {
      // TODO: implement me!
      val q = new SequentialStack[Int]
      assert(q.size == 0 & q.isEmpty)
    }

    it("should provide the semantics of pop() consistent with size() and isEmp()") {
      // TODO: implement me!
      val q = new SequentialStack[Int]
      q.push(1)
      assert(q.size > 0 & !(q.isEmpty))
      q.pop // when size of stack is greater than 0 (and not empty), pop outputs Some(value)
      assert(q.isEmpty & q.size == 0) // stack is empty
      assert(q.pop.isEmpty) // The result of q.pop is None
    }

    it("should provide the semantics of peek() consistent with pop()") {
      // TODO: implement me!
      val q = new SequentialStack[Int]
      q.push(1)
      assert(q.peek == q.pop)
    }

  }


}
