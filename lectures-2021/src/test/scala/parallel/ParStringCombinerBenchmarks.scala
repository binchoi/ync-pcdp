package parallel

import parallel.parstring.ParString

import scala.util.Random

/**
  * Example 19: ParString Combiner benchmarks
  */
object ParStringCombinerBenchmarks extends App {

  val txt = Random.shuffle(("A custom text " * 25000).toVector).mkString("")
  val partxt = new ParString(txt)

  val seqtime = warmedTimed(250) {
    txt.filter(x => x != ' ' && x != 'a')
  }

  val partime = warmedTimed(250) {
    partxt.filter(x => x != ' ' && x != 'a')
  }

  println(s"Sequential time - $seqtime ms")
  println(s"Parallel time   - $partime ms")

}
