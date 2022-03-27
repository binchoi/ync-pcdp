package homework.problem5

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class Problem5Tests extends FunSpec with Matchers {

  /** *********************************************************
    * Concurrent Queue tests 
    * ********************************************************/


  describe("The concurrent queue") {
    it("should exhibit FIFO properties with multiple threads") {
      val q = new ConcurrentQueue[Int]

      var enq_order = List()

      val e1 = new Enqueuer(q,1, enq_order)
      val e2 = new Enqueuer(q,11, enq_order)

      var deq_order = List()

      val d1 = new Dequeuer(q, deq_order)
      val d2 = new Dequeuer(q, deq_order)
      
      // TODO: your goal here is to check that the order of elements
      // is preserved when dequeued concurrently. Specifically:
      // if x has been enqueued before y and 
      // both x and y were dequeued in the same thread, then they 
      // were dequeued in the _same order_

      // TODO: implement the rest
      // Use start() and join() to synchronise the effects of the threads
      // You might want to make use of Dequeuer's `accumulated` method to
      // check what it has obtained
      e1.start()
      e2.start()

      e1.join()
      e2.join()

      d1.start()
      d2.start()

      d1.join()
      d2.join()

      assert(enq_order == deq_order) // enq_order and deq_order is the same
    }

    it("should not create spurious elements in the presence of concurrency") {
      // TODO: using a structure similar to the above, ensure that what
      // was dequeued is that same as was enqueued

      // same set up as before...
      val q = new ConcurrentQueue[Int]

      var enq_order = List()

      val e1 = new Enqueuer(q,1, enq_order)
      val e2 = new Enqueuer(q,11, enq_order)

      var deq_order = List()

      val d1 = new Dequeuer(q, deq_order)
      val d2 = new Dequeuer(q, deq_order)

      e1.start()
      e2.start()

      e1.join()
      e2.join()

      d1.start()
      d2.start()

      d1.join()
      d2.join()

      assert(enq_order sameElements deq_order) // same elements that were enqueued are dequeued. No spurious elements
    }
  }


  // TODO: feel free to modify this class by providing extra information
  // on what it should enqueue via its parameters
  class Enqueuer(val queue: ConcurrentQueue[Int], val initial: Int, var order: List[Int]) extends Thread {

    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      // TODO: enqeue several elements into the queue
      val end = initial + 9
      for (i <- initial to end) {
        queue.enq(i)
        println(s"Thread $id: Enqueued $i")
        order = i :: order
      }
    }
  }

  class Dequeuer(val queue: ConcurrentQueue[Int], var order: List[Int]) extends Thread {

    private var myAccumulator: List[Int] = Nil

    def accumulated: List[Int] = myAccumulator

    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      // TODO: dequeue several elements from the queue
      var result: Option[Int] = None
      do {
        result = queue.deq
        result match {
          case Some(value) =>
            println(s"Thread $id: Dequeued $value")
            order = value :: order
          case None =>
        }
      } while (result.isDefined)
    }
  }

  /** *********************************************************
    * Concurrent Stack tests 
    * ********************************************************/

  describe("The concurrent stack") {
    it("should exhibit LIFO properties with multiple threads") {
      // TODO: engineer similarly to the FIFO test for queues
      val q = new ConcurrentStack[Int]

      var push_order = List()

      val e1 = new Pusher(q,1, push_order)
      val e2 = new Pusher(q,11, push_order)

      var pop_order = List()

      val d1 = new Popper(q, pop_order)
      val d2 = new Popper(q, pop_order)

      e1.start()
      e2.start()

      e1.join()
      e2.join()

      d1.start()
      d2.start()

      d1.join()
      d2.join()

      assert(pop_order == push_order.reverse) // LIFO property - order of pop/push is reversed
    }

    it("should not create spurious elements in the presence of concurrency") {
      // TODO: engineer similarly to the test for queues
      val q = new ConcurrentStack[Int]

      var push_order = List()

      val e1 = new Pusher(q,1, push_order)
      val e2 = new Pusher(q,11, push_order)

      var pop_order = List()

      val d1 = new Popper(q, pop_order)
      val d2 = new Popper(q, pop_order)

      e1.start()
      e2.start()

      e1.join()
      e2.join()

      d1.start()
      d2.start()

      d1.join()
      d2.join()

      assert(pop_order sameElements push_order)
      // elements that were popped are same as elements that were pushed
      // - no spurious elements
    }
  }


  class Pusher(val stack: ConcurrentStack[Int], val initial: Int, var push_order: List[Int]) extends Thread {
    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      // TODO: push several elements into the stack
      val end = initial + 9
      for (i <- initial to end) {
        stack.push(i)
        println(s"Thread $id: Pushed $i")
        push_order = i :: push_order
      }
    }
  }

  class Popper(val stack: ConcurrentStack[Int], var pop_order: List[Int]) extends Thread {
    private var myAccumulator: List[Int] = Nil

    def accumulated: List[Int] = myAccumulator

    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      // TODO: pop several elements from the stack
      var result: Option[Int] = None
      do {
        result = stack.pop
        result match {
          case Some(value) =>
            println(s"Thread $id: Popped $value")
            pop_order = value :: pop_order
          case None =>
        }
      } while (result.isDefined)
    }
  }

}
