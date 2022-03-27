package parallel.basic

import parallel._

/**
  * MAX is a particularly good function to parallelise on vectors 
  * 
  * Example 04: Good parallelism
  */
object GoodParallelism extends App {

  val numbers = scala.util.Random.shuffle(Vector.tabulate(5000000)(i => i))
  
  var n1, n2: Int = 0
  
  val partime = warmedTimed() {
    n1 = numbers.par.max
    // println(s"largest number $n")
  }
  
  val seqtime = warmedTimed() {
    n2 = numbers.max
    // println(s"largest number $n")
  }
  
  assert(n1 == n2)
  
  println(s"Sequential time $seqtime ms")
  println(s"Parallel time $partime ms")
}