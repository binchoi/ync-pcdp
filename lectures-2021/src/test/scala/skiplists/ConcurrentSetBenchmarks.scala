package skiplists

import lists.{ConcurrentSet, LazyList, OptimisticList}
import util.ThreadID

/**
 * Compare the performance of concurrent sets
 */
object ConcurrentSetBenchmarks {

  def main(args: Array[String]): Unit = {
    // Comparing the three implementation
    runBenchmark(new OptimisticList[Int])
    runBenchmark(new LazyList[Int])

    runBenchmark(new SynchronizedSkipList[Int])
    runBenchmark(new LazySkipList[Int])

    // Also adding a benchmark for Java's `ConcurrentSkipListSet`
    runBenchmark(new JavaSkipListSet[Int])
  }

  /**
   * Running benchmarks for a specific concurrent set implementation 
   */
  def runBenchmark(set: ConcurrentSet[Int], warmUp: Boolean = false): Unit = {

    ThreadID.reset()
    val (_, adders, checkers, remover) = mkThreads(set)
    val threads = remover :: adders ++ checkers

    val t1 = System.currentTimeMillis()
    for (t <- threads) t.start()
    for (t <- threads) t.join()
    val t2 = System.currentTimeMillis()

    // val allWitnessed = checkers.map(_.witnessed).toSet.flatten
    // val allRemoved = remover.removed
    //    for (r <- allRemoved) {
    //      assert(!set.contains(r))
    //    }
    //
    //    for (r <- allWitnessed if !allRemoved.contains(r)) {
    //      assert(set.contains(r))
    //    }
    //
    //    for (e <- input) {
    //      assert(set.contains(e) && !allRemoved.contains(e) ||
    //        allRemoved.contains(e) && !set.contains(e))
    //    }

    if (!warmUp) {
      val formatter = java.text.NumberFormat.getIntegerInstance

      println(s"Statistics for ${set.getClass.getSimpleName}:")
      println(s"Number of threads: ${NUM_THREADS * 2 + 1}")
      println(s"Total time:        ${formatter.format(t2 - t1)} ms")
      println()
    }
  }

  ///////////////////////////////////////////////////////////
  // TODO: Auxiliary functions
  ///////////////////////////////////////////////////////////


  val INPUT_SIZE = 3000
  val NUM_THREADS = 4

  def mkThreads(s: ConcurrentSet[Int]) = {
    val inputs = (for (i <- 0 until NUM_THREADS) yield {
      val start = i * INPUT_SIZE
      val end = (i + 1) * INPUT_SIZE - 1
      (start to end).toList
    }).toList

    val adders = for (in <- inputs) yield new Adder(s, in)
    val checkers = for (in <- inputs) yield new Checker(s, in)
    val remover = new Remover(s, inputs.flatten)
    (inputs.flatten, adders, checkers, remover)
  }

  ///////////////////////////////////////////////////////////
  // TODO: Useful threads --- feel free to modify
  ///////////////////////////////////////////////////////////

  class Adder(val set: ConcurrentSet[Int], input: List[Int]) extends Thread {
    override def run() = {
      for (i <- input.reverse) {
        set.add(i)
      }
    }
  }

  class Remover(val set: ConcurrentSet[Int], toRemove: List[Int]) extends Thread {
    var removed: Set[Int] = Set.empty

    override def run() = {
      for (i <- toRemove) {
        if (set.remove(i)) {
          removed = removed + i
        }
      }
    }
  }

  class Checker(val set: ConcurrentSet[Int], elems: List[Int]) extends Thread {
    var witnessed: Set[Int] = Set.empty

    override def run() = {
      for (i <- elems) {
        if (set.contains(i)) {
          witnessed = witnessed + i
        }
      }
    }
  }

}
