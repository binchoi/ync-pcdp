/**
  * Utility methods
  */
package object parheap {

  @volatile var dummy: Any = _

  def timed[T](body: => T): Double = {
    val start = System.nanoTime
    dummy = body
    val end = System.nanoTime
    ((end - start) / 1000) / 1000.0
  }

  def warmedTimed[T](times: Int = 10)(body: => T): Double = {
    for (_ <- 0 until times) body
    timed(body)
  }

}
