package stacks.benchmarks

import concurrent.EmptyException
import monitors.counting.CountManyThreads.println
import stacks.ConcurrentStack

/**
  * @author Ilya Sergey
  */
trait StackBenchmarks {

  val CHUNK_SIZE = 500000

  def genChunk(i: Int) = ((i - 1) * CHUNK_SIZE + 1 to i * CHUNK_SIZE).toList

  def mkStack(capacity: Int): ConcurrentStack[Int]

  val PUSH_NUM = 2
  val POP_NUM = 2


  def execute(warmed: Boolean = true): Unit = {
    val q = mkStack(CHUNK_SIZE / 10)

    val enqueuers = for {
      i <- 1 to PUSH_NUM
      input = genChunk(i)
    } yield new Pusher(input, q)

    val dequeuers = for {
      i <- 1 to PUSH_NUM
      input = genChunk(i)
    } yield new Popper(q, genChunk(i).size)

    val t1 = System.currentTimeMillis()
    // Start all threads
    for (t <- enqueuers ++ dequeuers) {
      t.start()
    }

    for (t <- enqueuers ++ dequeuers) {
      t.join()
    }

    val t2 = System.currentTimeMillis()

    if (warmed) {
      val formatter = java.text.NumberFormat.getIntegerInstance
      println()
      println(s"Time for ${q.getClass.getSimpleName}: ${formatter.format(t2 - t1)} ms")
    }
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

    def getResult = result.reverse

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

