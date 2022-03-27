package parallel.basic

import java.util.concurrent.atomic.AtomicLong

import parallel._

/**
  * Example 06: Bad parallelism
  *
  */
object BadParallelism {

  def main(args: Array[String]): Unit = {
    var uid = new AtomicLong(0L)

    // Run sequentially
    val seqtime = warmedTimed() {
      for (i <- 0 until 10000000) uid.incrementAndGet()
    }

    uid = new AtomicLong(0L)
    // Run in parallel
    val partime = warmedTimed() {
      for (i <- (0 until 10000000).par) uid.incrementAndGet()
    }

    println(s"Sequential time $seqtime ms")
    println(s"Parallel time $partime ms")
  }
}
