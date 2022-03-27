package counting

import mutex.TreeLock

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class Counter(val numThr: Int,
              val totalCount: Int = 1000000) extends Runnable {

  /* 
		When a million is not evenly divisible my numThr, the final result 
		will not be a million. That should be okay as long as the final counter 
		output is equal to ((1000000 / numThr) * numThr) every time you run the 
		program
	 */

  private var count = 0
  private val timesThrIncs = totalCount / numThr
  private val tlock = new TreeLock(numThr)

  override def run(): Unit = {
    for (i <- 0 until timesThrIncs) {
      tlock.lock()
      count = count + 1
      tlock.unlock()
    }
  }
  
  def getCount: Int = count
}