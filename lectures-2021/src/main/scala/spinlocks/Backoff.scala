package spinlocks

import scala.util.Random

/**
  * @author Ilya Sergey
  */
class Backoff(val minDelay: Int, maxDelay: Int) {
  val random = new Random()
  private var limit = minDelay
  
  @throws[InterruptedException]
  def backoff(): Unit = {
    val delay = random.nextInt(limit)
    limit = Math.min(maxDelay, 2 * limit)
    Thread.sleep(0, delay)
  }
  
}
