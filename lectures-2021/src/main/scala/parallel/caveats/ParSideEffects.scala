package parallel.caveats

import java.util.concurrent.atomic.AtomicInteger

/**
  * Example 09: Side effects in parallel collections
  */

object ParSideEffectsIncorrect extends App {

  import scala.collection._

  private val size = 10000

  def intersectionSize(a: GenSet[Int], b: GenSet[Int]): Int = {
    var count = new AtomicInteger(0)
//    for (x <- a) if (b contains x) count += 1
    a.foreach(x => if (b contains x) count.incrementAndGet())
    count.intValue()
  }
  
  val seqres = intersectionSize((0 until size).toSet, (0 until size by 4).toSet)
  val parres = intersectionSize((0 until size).par.toSet, (0 until size by 4).par.toSet)
  println(s"Sequential result - $seqres")
  println(s"Parallel result   - $parres")
}



object ParSideEffectsCorrect extends App {

  import scala.collection._
  import java.util.concurrent.atomic._

  private val size = 10000

  def intersectionSize(a: GenSet[Int], b: GenSet[Int]): Int = {
    a.count(e => b.contains(e))
  }
  
  val seqres = intersectionSize((0 until size).toSet, (0 until size by 4).toSet)
  val parres = intersectionSize((0 until size).par.toSet, (0 until size by 4).par.toSet)
  println(s"Sequential result - $seqres")
  println(s"Parallel result   - $parres")
}
