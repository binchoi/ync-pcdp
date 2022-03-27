package stacks.benchmarks

import stacks.{EliminationBackoffStack, LockFreeStack, SynchronizedStack}

/**
 * @author Ilya Sergey
 */
object StackBenchmarkRunner {

  val sync = new StackBenchmarks {
    override def mkStack(capacity: Int) = new SynchronizedStack[Int]
  }

  val lockFree = new StackBenchmarks {
    override def mkStack(capacity: Int) = new LockFreeStack[Int]
  }

  val elimination = new StackBenchmarks {
    override def mkStack(capacity: Int) = new EliminationBackoffStack[Int]
  }

  def main(args: Array[String]): Unit = {
    println("Warming up...")
    sync.execute(false)
    lockFree.execute(false)
    elimination.execute(false)
    println("Done warming up, benchmarking now.")

    sync.execute()
    lockFree.execute()
    // elimination.execute()
  }
}