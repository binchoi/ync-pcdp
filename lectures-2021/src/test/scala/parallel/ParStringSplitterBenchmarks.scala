package parallel

import parallel.parstring.ParString

import scala.util.Random

/**
  * Example 16: ParString Splitter benchmarks
  */
object ParStringSplitterBenchmarks {
  
  def main(args: Array[String]): Unit = {
    
    val text = Random.shuffle(("A custom text " * 250000).toVector).mkString("")
    val partxt = new ParString(text)

    var seqResult = -1
    val seqtime = warmedTimed(50) {
      seqResult = text.count(Character.isUpperCase)
    }

    var parResult = -1
    val partime = warmedTimed(50) {
      parResult = partxt.count(Character.isUpperCase)
    }

    assert(parResult == seqResult)

    println(s"Sequential time - $seqtime ms")
    println(s"Parallel time   - $partime ms")

  }

}
