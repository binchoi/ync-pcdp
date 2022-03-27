package parallel.caveats

/**
  * Example 10: Non-commutative operations
  */
object ParNonCommutativeOperator extends App {

  import scala.collection._

  val doc = mutable.ArrayBuffer.tabulate(20)(i => s"Page $i, ")

  // String concatenation is NOT commutative
  def test(doc: GenIterable[String]) {
    val seqtext = doc.seq.reduceLeft(_ + _)
    val partext = doc.par.reduce(_ + _)

    // The results are likely to be different for non-order preserving collections (i.e., sets)
    println(s"Sequential result: $seqtext\n")
    println(s"Parallel result:   $partext\n")
  }

  // TODO: Test on ArrayBuffers
   //test(doc)
  
  // TODO Test on sets
   test(doc.toSet)
}