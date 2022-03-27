package queues.benchmarks

import concurrent.{ConcurrentQueue, EmptyException}
import monitors.counting.CountManyThreads.println

/**
  * @author Ilya Sergey
  */
trait QueueBenchmarks {

  val CHUNK_SIZE = 1000000

  def genChunk(i: Int) = ((i - 1) * CHUNK_SIZE + 1 to i * CHUNK_SIZE).toList

  def mkQueue(capacity: Int): ConcurrentQueue[Int]
  
  val ENQ_NUM = 2
  val DEQ_NUM = 2

  
  def execute(warmup: Boolean = true): Unit = {
    val q = mkQueue(CHUNK_SIZE / 10)
    
    val enqueuers = for {
      i <- 1 to ENQ_NUM
      input = genChunk(i)
    } yield new Enqueuer(input, q)
    
    val dequeuers = for {
      i <- 1 to ENQ_NUM
      input = genChunk(i)
    } yield new Dequeuer(q, genChunk(i).size)
    
    val t1 = System.currentTimeMillis()
    // Start all threads
    for (t <- enqueuers ++ dequeuers) {
      t.start()
    }

    for (t <- enqueuers ++ dequeuers) {
      t.join()
    }

    val t2 = System.currentTimeMillis()
    
    if (warmup) {
      val formatter = java.text.NumberFormat.getIntegerInstance
      println()
      println(s"Time for ${q.getClass.getSimpleName}: ${formatter.format(t2 - t1)} ms")
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
