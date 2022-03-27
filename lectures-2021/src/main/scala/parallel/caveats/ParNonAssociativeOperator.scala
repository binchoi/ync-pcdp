package parallel.caveats

/**
  * Example 11
  */
object ParNonAssociativeOperator extends App {
  import scala.collection._

  def test(doc: GenIterable[Int]) {
    val seqtext = doc.seq.reduceLeft(_ - _)
    val partext = doc.par.reduce(_ - _)
    
    println(s"Sequential result - $seqtext\n")
    println(s"Parallel result   - $partext\n")
  }
  test(0 until 30)
}
