package multiset

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

import scala.util.Random

/**
  * @author Ilya Sergey
  */
trait HashCollistionTests extends FunSpec with Matchers {
//  val printStats = true

  val MY_NUM_THREADS = 4
  val MY_INPUT_SIZE = 60

  def mkCollisionFreeSet: ConcurrentMultiSet[MyObject]

  describe(s"Concurrent multi-set ${this.getClass.getName}") {
    it("should handle different elements with same hashes") {
      // TODO: Implement me by overloading .hashCode in MyObject
      ThreadID.reset()
      // Handling unique elements
      val set = mkCollisionFreeSet
      val (input, adders, checkers, remover) = mkThreadsHC(set)
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

      println((allRemoved).size)
      println((allWitnessed).size)
      println((set).count(new MyObject(10)))


      for (e <- input) {
      assert((set.contains(e) && !allRemoved.contains(e)) ||
      (allRemoved.contains(e) && !set.contains(e)))
      }

      val formatter = java.text.NumberFormat.getIntegerInstance

      val timeAdd = adders.map(_.time).sum
      val timeRemove = remover.time
      val timeCheck = checkers.map(_.time).sum

      //      if (printStats) {
      println()
      println(s"Statistics for ${set.getClass.getName}:")
      println(s"Number of threads: ${MY_NUM_THREADS * 2 + 1}")
      println(s"Input size:        ${input.size}")
      println(s"Adding time:       ${formatter.format(timeAdd)} ms")
      println(s"Removing time:     ${formatter.format(timeRemove)} ms")
      println(s"Checking time:     ${formatter.format(timeCheck)} ms")
      println(s"Total time:        ${formatter.format(t2 - t1)} ms")
      //      }

    }
  }

  // A class used to test multi-set in the presence of hash-code collisions
  class MyObject(val fieldOne: Int) {
    override def hashCode() = {
      this.fieldOne % 10
    }
  }
  
  // TODO: Add implementations of specific threads, used for testing
  def mkThreadsHC(s: ConcurrentMultiSet[MyObject]) = {
    val inputs = (for (i <- 0 until MY_NUM_THREADS) yield {
      List.fill(MY_INPUT_SIZE)(new MyObject(i))
    }).toList

    val adders = for (in <- inputs) yield new Adder(s, in)
    val checkers = for (in <- inputs) yield new Checker(s, in)
    val remover = new Remover(s, inputs.flatten)
    (inputs.flatten, adders, checkers, remover)
  }
//
//  def mkThreadsMultiSet(s: ConcurrentMultiSet[MyObject]) = {
//    val inputs = (for (i <- 0 until MY_NUM_THREADS) yield {
//      val start = 0
//      val end = MY_INPUT_SIZE - 1 // overlap introduced
//      (start to end).toList
//    }).toList
//
//    val adders = for (in <- inputs) yield new Adder(s, in)
//    val checkers = for (in <- inputs) yield new Checker(s, in)
//    val remover = new Remover(s, inputs.flatten)
//    (inputs.flatten, adders, checkers, remover)
//  }


  class Adder(val set: ConcurrentMultiSet[MyObject], input: List[MyObject]) extends Thread {
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

  class Remover(val set: ConcurrentMultiSet[MyObject], toRemove: List[MyObject]) extends Thread {
    var removed: Set[MyObject] = Set.empty
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

  class Checker(val set: ConcurrentMultiSet[MyObject], elems: List[MyObject]) extends Thread {
    var witnessed: Set[MyObject] = Set.empty
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

//
//ThreadID.reset()
//// Handling unique elements
//val set = mkCollisionFreeSet
//val (input, adders, checkers, remover) = mkThreadsHC(set)
//val threads = adders ++ checkers ++ List(remover)
//
//val t1 = System.currentTimeMillis()
//for (t <- adders) t.start()
//for (t <- adders) t.join()
//for (t <- remover :: checkers) t.start()
//for (t <- remover :: checkers) t.join()
//val t2 = System.currentTimeMillis()
//
//val allWitnessed = checkers.map(_.witnessed).toSet.flatten
//val allRemoved = remover.removed
//
//for (r <- allRemoved) {
//assert(!set.contains(r))
//}
//
//for (r <- allWitnessed if !allRemoved.contains(new MyObject(r))) {
//assert(set.contains(new MyObject(r)))
//}
//
//println((allRemoved).size)
//println((allWitnessed).size)
//println((set).count(new MyObject(10)))
//
//
//for (e <- input) {
//assert((set.contains(new MyObject(e)) && !allRemoved.contains(new MyObject(e))) ||
//(allRemoved.contains(new MyObject(e)) && !set.contains(new MyObject(e))))
//}
//
//val formatter = java.text.NumberFormat.getIntegerInstance
//
//val timeAdd = adders.map(_.time).sum
//val timeRemove = remover.time
//val timeCheck = checkers.map(_.time).sum
//
////      if (printStats) {
//println()
//println(s"Statistics for ${set.getClass.getName}:")
//println(s"Number of threads: ${MY_NUM_THREADS * 2 + 1}")
//println(s"Input size:        ${input.size}")
//println(s"Adding time:       ${formatter.format(timeAdd)} ms")
//println(s"Removing time:     ${formatter.format(timeRemove)} ms")
//println(s"Checking time:     ${formatter.format(timeCheck)} ms")
//println(s"Total time:        ${formatter.format(t2 - t1)} ms")
////      }