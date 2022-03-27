package parallel.caveats

import parallel._

/**
  * Example 07: Non-parallelizable collections 
  */
object NonParallelizableCollections extends App {

  private val largeNumber = 6000000
  
  val vector = Vector.fill(largeNumber)("")
  val list = List.fill(largeNumber)("")
  
  private val listtime = warmedTimed()(list.par)
  private val vectortime = warmedTimed()(vector.par)
  
  println(s"list conversion time: $listtime ms")
  println(s"vector conversion time: $vectortime ms")
}