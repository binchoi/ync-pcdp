package stacks

import concurrent.EmptyException
import monitors.counting.CountManyThreads.println
import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
trait ConcurrentStackTests extends FunSpec with Matchers {
  val CHUNK_SIZE = 100000

  def genChunk(i: Int) = ((i - 1) * CHUNK_SIZE + 1 to i * CHUNK_SIZE).toList

  def mkStack(capacity: Int): ConcurrentStack[Int]

  describe(s"A concurrent stack ${this.getClass.getName}") {
    it("should handle multiple pushers") {
      val stack = mkStack(CHUNK_SIZE * 3 + 1)

      val input1 = genChunk(1)
      val input2 = genChunk(2)
      val input3 = genChunk(3)
      val t1 = new Pusher(input1, stack)
      val t2 = new Pusher(input2, stack)
      val t3 = new Pusher(input3, stack)
      val input = input1 ++ input2 ++ input3

      // TODO: Implement me!

      // Start all threads
      t1.start()
      t2.start()
      t3.start()

      // Wait for all threads to join
      t1.join()
      t2.join()
      t3.join()

      val result = stack.toListThreadUnsafe
      //println(result)
      assert(result.toSet == input.toSet)
    }
  }

  describe(s"A concurrent stack ${this.getClass.getName}") {
    it("should handle a single pusher and single popper") {
      val q = mkStack(CHUNK_SIZE / 10)

      val input = genChunk(1)
      val t1 = new Pusher(input, q)
      val t2 = new Popper(q, input.size)

      // Start all threads
      t2.start()
      t1.start()

      // Wait for all threads to join
      t1.join()
      t2.join()

      val result = t2.getResult
      //println(result)
      assert(result.sorted == input)
    }
  }


  describe(s"A concurrent stack ${this.getClass.getName}") {
    it("should handle a many pushers and many poppers") {
      val q = mkStack(CHUNK_SIZE / 10)

      val input1 = genChunk(1)
      val input2 = genChunk(2)
      val e1 = new Pusher(input1, q)
      val e2 = new Pusher(input2, q)
      val d1 = new Popper(q, input1.size)
      val d2 = new Popper(q, input2.size)

      // TODO: Implement me!

      val t1 = System.currentTimeMillis()
      // Start all threads
      d2.start()
      e1.start()
      d1.start()
      e2.start()

      // Wait for all threads to join
      e1.join()
      d1.join()
      d2.join()
      e2.join()
      val t2 = System.currentTimeMillis()

      val result1 = d1.getResult
      val result2 = d2.getResult

      assert((result1 ++ result2).sorted == input1 ++ input2)

//      val formatter = java.text.NumberFormat.getIntegerInstance
//      println()
//      println(s"Time for ${q.getClass.getSimpleName}: ${formatter.format(t2 - t1)} ms")
    }
  }

  def checkOrder(input: List[Int], result: List[Int]): Boolean = {

    // Taking advantae of input being sorted
    val projection = result.filter(input.contains(_))
    val rev = projection.sorted.reverse
    val c = rev == projection
    if (!c) {
      println(s"$rev")
      println(s"$projection")
      println(s"${rev.zip(projection).filter{case (x, y) => x != y}}")
    }
    c
  }


  class Pusher(elems: List[Int], queue: ConcurrentStack[Int]) extends Thread {
    override def run(): Unit = {
      for (e <- elems) {
        queue.push(e)
        // println(s"Enqueued $e")
      }
    }
  }

  class Popper(queue: ConcurrentStack[Int], howMany: Int) extends Thread {

    private var result: List[Int] = Nil

    def getResult = result

    override def run(): Unit = {

      var i = 0
      while (i < howMany) {
        try {
          val r = queue.pop()
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