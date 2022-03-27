package parallel.caveats

import parallel._

import scala.collection._

/**
  * Example 08: Non-parallelizable operations
  */
object NonParallelizableOperations extends App {

  val vec = 1 to 1000000

  def allMatches(d: GenSeq[Int]) = warmedTimed(20) {
    
    // TODO: find all palindromes in  numbers `vec` via foldLeft
    """
      |1
      |2
      |...
      |11
      |22
      |33
      |..
      |""".stripMargin
    
    // d.foldLeft("")((acc, n) => if (n.toString == n.toString.reverse) s"$acc\n$n" else acc)
    // What about using "aggregate" instead of "foldLeft"!
    d.aggregate("")((acc, n) => if (n.toString == n.toString.reverse) s"$acc\n$n\n" else acc,
      (x, y) => x ++ y)
  }

  // Measure for sequential vector
  val seqtime = allMatches(vec)

  // Measure for parallel vector
  val partime = allMatches(vec.par)

  println(s"Sequential time - $seqtime ms")
  println(s"Parallel time   - $partime ms")
}
