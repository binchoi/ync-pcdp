package parallel.basic

import parallel.timed

/**
  * Example 02
  * 
  * Timing sequential and concurrent collections
  * 
  * @author Ilya Sergey
  */
object ParDemoTimed {

  def main(args: Array[String]): Unit = {
    // The timing is order-dependent.

    // TODO: Try swapping these two and see how the times change.
    val t1 = timed {
      (0 until 1000000).filter(x => x.toString == x.toString.reverse)
    }

    val t2 = timed {
      (0 until 1000000).par.filter(x => x.toString == x.toString.reverse)
    }

    val formatter = java.text.NumberFormat.getNumberInstance
    println(s"Sequential time: ${formatter.format(t1)} ms")
    println(s"Parallel time:   ${formatter.format(t2)} ms")
  }

}
