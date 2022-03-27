package parallel.basic

import parallel.warmedTimed

/**
  * Example 05: Configuring the level of parallelism
  *
  * @author Aleksandar Prokopec, Ilya Sergey
  */
object ParConfig extends App {

  import scala.collection._
  import scala.concurrent.forkjoin.ForkJoinPool

  val fjpool = new ForkJoinPool(2)
  val myTaskSupport = new parallel.ForkJoinTaskSupport(fjpool)
  val numbers = scala.util.Random.shuffle(Vector.tabulate(5000000)(i => i))

  var n1, n2 = 0

  println("Computing MAX in parallel")
  val partime = warmedTimed() {
    val parnumbers = numbers.par

    // Assigning a number of threads
    parnumbers.tasksupport = myTaskSupport
    n1 = parnumbers.max

  }

  println("Computing MAX sequentially")
  val seqtime = warmedTimed() {
    n2 = numbers.max
  }

  assert(n1 == n2)

  println()
  println(s"largest number $n1")
  println(s"Parallel time $partime ms")
  println(s"Sequential time $seqtime ms")
}