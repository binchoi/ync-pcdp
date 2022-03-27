package multiset

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

trait MultiSetTests extends FunSpec with Matchers {
  val printStats = true

  val NUM_THREADS = 4
  val INPUT_SIZE = 6000

  def mkSet: ConcurrentMultiSet[Int]

  describe(s"Concurrent multi-set ${this.getClass.getName}") {
    it("should behave correctly as a set") {
      ThreadID.reset()
      // Handling unique elements
      val set = mkSet
      val (input, adders, checkers, remover) = mkThreads(set)
      val threads = adders ++ checkers ++ List(remover)

      val t1 = System.currentTimeMillis()
      for (t <- adders) t.start()
      for (t <- adders) t.join()
      for (t <- remover :: checkers) t.start()
      for (t <- remover :: checkers) t.join()
      val t2 = System.currentTimeMillis()

      val allWitnessed = checkers.map(_.witnessed).toSet.flatten
      val allRemoved = remover.removed

      for (r <- allRemoved) {
        assert(!set.contains(r))
      }

      for (r <- allWitnessed if !allRemoved.contains(r)) {
        assert(set.contains(r))
      }

      for (e <- input) {
        assert(set.contains(e) && !allRemoved.contains(e) ||
          allRemoved.contains(e) && !set.contains(e))
      }

      val formatter = java.text.NumberFormat.getIntegerInstance

      val timeAdd = adders.map(_.time).sum
      val timeRemove = remover.time
      val timeCheck = checkers.map(_.time).sum

      if (printStats) {
        println()
        println(s"Statistics for ${set.getClass.getName}:")
        println(s"Number of threads: ${NUM_THREADS * 2 + 1}")
        println(s"Input size:        ${input.size}")
        println(s"Adding time:       ${formatter.format(timeAdd)} ms")
        println(s"Removing time:     ${formatter.format(timeRemove)} ms")
        println(s"Checking time:     ${formatter.format(timeCheck)} ms")
        println(s"Total time:        ${formatter.format(t2 - t1)} ms")
      }

//      println(allRemoved.size + " " + allWitnessed.size)
    }
  }

  describe(s"Concurrent multi-set ${this.getClass.getName}") {
    it("should behave correctly as a multi-set") {
      ThreadID.reset()
      // Handling overlapping elements
      val set = mkSet
      val (input, adders, checkers, remover) = mkThreadsMultiSet(set)
      val threads = adders ++ checkers ++ List(remover)

      val t1 = System.currentTimeMillis()
      for (t <- adders) t.start()
      for (t <- adders) t.join()
      for (t <- remover :: checkers) t.start()
      for (t <- remover :: checkers) t.join()
      val t2 = System.currentTimeMillis()

      val allWitnessed = checkers.map(_.witnessed).toSet.flatten
      val allRemoved = remover.removed

      for (r <- allRemoved) {
        assert(!set.contains(r))
      }

      for (r <- allWitnessed if !allRemoved.contains(r)) {
        assert(set.contains(r))
      }

      for (e <- input) {
        assert(set.contains(e) && !allRemoved.contains(e) ||
          allRemoved.contains(e) && !set.contains(e))
      }

      val formatter = java.text.NumberFormat.getIntegerInstance

      val timeAdd = adders.map(_.time).sum
      val timeRemove = remover.time
      val timeCheck = checkers.map(_.time).sum

      if (printStats) {
        println()
        println(s"Statistics for ${set.getClass.getName}:")
        println(s"Number of threads: ${NUM_THREADS * 2 + 1}")
        println(s"Input size:        ${input.size}")
        println(s"Adding time:       ${formatter.format(timeAdd)} ms")
        println(s"Removing time:     ${formatter.format(timeRemove)} ms")
        println(s"Checking time:     ${formatter.format(timeCheck)} ms")
        println(s"Total time:        ${formatter.format(t2 - t1)} ms")
      }
    }
  }


  def mkThreads(s: ConcurrentMultiSet[Int]) = {
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

  def mkThreadsMultiSet(s: ConcurrentMultiSet[Int]) = {
    val inputs = (for (i <- 0 until NUM_THREADS) yield {
      val start = 0
      val end = INPUT_SIZE - 1 // overlap introduced
      (start to end).toList
    }).toList

    val adders = for (in <- inputs) yield new Adder(s, in)
    val checkers = for (in <- inputs) yield new Checker(s, in)
    val remover = new Remover(s, inputs.flatten)
    (inputs.flatten, adders, checkers, remover)
  }

  class Adder(val set: ConcurrentMultiSet[Int], input: List[Int]) extends Thread {
    var time: Long = 0

    override def run() = {
      val t1 = System.currentTimeMillis()
      val perm = input.permutations.take(10000).next
      for (i <- perm) {
        set.add(i)
//        println(i)
      }
      val t2 = System.currentTimeMillis()
      time = t2 - t1
    }
  }

  class Remover(val set: ConcurrentMultiSet[Int], toRemove: List[Int]) extends Thread {
    var removed: Set[Int] = Set.empty
    var time: Long = 0

    override def run() = {
      val t1 = System.currentTimeMillis()
      for (i <- toRemove) {
        if (set.remove(i)) {
          removed = removed + i
        }
        val t2 = System.currentTimeMillis()
        time = t2 - t1
      }
    }
  }

  class Checker(val set: ConcurrentMultiSet[Int], elems: List[Int]) extends Thread {
    var witnessed: Set[Int] = Set.empty
    var time: Long = 0

    override def run() = {
      val t1 = System.currentTimeMillis()
      for (i <- elems) {
        if (set.contains(i)) {
          witnessed = witnessed + i
        }
      }
      val t2 = System.currentTimeMillis()
      time = t2 - t1
    }


  }

}