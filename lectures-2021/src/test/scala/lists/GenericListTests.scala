package lists

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * @author Ilya Sergey
  */
trait GenericListTests extends FunSpec with Matchers {
  val printStats = true

  val NUM_THREADS = 4
  val INPUT_SIZE = 6000

  def mkSet: ConcurrentSet[Int]

  describe(s"ConcurrentList in ${this.getClass.getName}") {
    it("should behave correctly") {
      ThreadID.reset()
      val set = mkSet
      val (input, adders, checkers, remover) = mkThreads(set)
      val threads = adders ++ checkers ++ List(remover)

      val t1 = System.currentTimeMillis()

      for (t <- adders) t.start()
      for (t <- adders) t.join()

      remover.start()
      for (t <- checkers) t.start()
      for (t <- checkers) t.join()
      remover.join()

      val failures = set.checkFailures

      val t2 = System.currentTimeMillis()

      val allWitnessed = checkers.map(_.witnessed).toSet.flatten
      val allRemoved = remover.removed
  
      // TODO: What should we check for `allRemoved`?
      for (r <- allRemoved) {
        assert(!set.contains(r))
      }

      // TODO: What should we check for `allWitnessed`?
      for (r <- allWitnessed) {
        assert(set.contains(r) || allRemoved.contains(r))
      }

//      // TODO: What should we check wrt. the initial set of elements `input`?
//      assert(false)
//

      //assert(false)

      val formatter = java.text.NumberFormat.getIntegerInstance

      val timeAdd = adders.map(_.time).sum
      val timeRemove = remover.time
      val timeCheck = checkers.map(_.time).sum

      if (printStats) {
        println()
        println(s"Statistics for ${set.getClass.getName}:")
        println(s"Number of threads: $NUM_THREADS adders, and then $NUM_THREADS checkers and 1 remover}")
        println(s"Input size:        ${input.size}")
        println(s"Adding time:       ${formatter.format(timeAdd)} ms")
        println(s"Removing time:     ${formatter.format(timeRemove)} ms")
        println(s"Checking time:     ${formatter.format(timeCheck)} ms")
        println(s"Total time:        ${formatter.format(t2 - t1)} ms")
        println(s"Checking failures: ${failures}")
      }


    }
  }


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


  class Adder(val set: ConcurrentSet[Int], input: List[Int]) extends Thread {
    var time: Long = 0

    override def run() = {
      val t1 = System.currentTimeMillis()
      // val perm = input.permutations.take(10000).next
      for (i <- input) {
        set.add(i)
      }
      val t2 = System.currentTimeMillis()
      time = t2 - t1
    }
  }

  class Remover(val set: ConcurrentSet[Int], toRemove: List[Int]) extends Thread {
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

  class Checker(val set: ConcurrentSet[Int], elems: List[Int]) extends Thread {
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
