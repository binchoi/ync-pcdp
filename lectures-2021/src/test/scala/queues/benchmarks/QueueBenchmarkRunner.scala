package queues.benchmarks

import queues._

/**
  * @author Ilya Sergey
  */
object QueueBenchmarkRunner {
  
  val bounded = new QueueBenchmarks {
    override def mkQueue(capacity: Int) = new BoundedQueue[Int](capacity)
  }

  val unbounded = new QueueBenchmarks {
    override def mkQueue(capacity: Int) = new UnboundedQueue[Int]
  }

  val sync = new QueueBenchmarks {
    override def mkQueue(capacity: Int) = new SynchronizedQueue[Int]
  }

  val lockFree = new QueueBenchmarks {
    override def mkQueue(capacity: Int) = new LockFreeQueue[Int]
  }

  def main(args: Array[String]): Unit = {
    // Warm-up
    println("Warming up...")
    sync.execute(false)
    lockFree.execute(false)
    bounded.execute(false)
    unbounded.execute(false)
    println("Done warming up, benchmarking now.")

    sync.execute()
    unbounded.execute()
    // bounded.execute()
    // lockFree.execute()
  }

}
