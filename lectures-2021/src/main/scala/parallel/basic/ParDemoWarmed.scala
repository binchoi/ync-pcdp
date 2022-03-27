package parallel.basic

import parallel.warmedTimed

/**
  * Example 03: Warmed-up timing
  * 
  * @author Ilya Sergey
  */
object ParDemoWarmed {

  def main(args: Array[String]): Unit = {
    
    // TODO: Now try swapping these two

    val t2 = warmedTimed(20) {
      (0 until 1000000).par.filter(x => x.toString == x.toString.reverse)
    }

    val t1 = warmedTimed(20) {
      (0 until 1000000).filter(x => x.toString == x.toString.reverse)
    }
    
    val formatter = java.text.NumberFormat.getNumberInstance
    println(s"Sequential time: ${formatter.format(t1)} ms")
    println(s"Parallel time:   ${formatter.format(t2)} ms")
  }


}
