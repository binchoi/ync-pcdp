package stacks

import scala.concurrent.TimeoutException
import scala.reflect.ClassTag
import scala.util.Random

/**
  * @author Ilya Sergey
  */
class EliminationArray[T: ClassTag] {
  val duration = 10
  private val size = (Runtime.getRuntime.availableProcessors + 1) / 2
  val exchanger = Array.fill(size)(new LockFreeExchanger[T])
  val random = new Random()
  
  @throws [TimeoutException]
  def visit(value: T, range: Int): T = {
    val slot = random.nextInt(range)
    exchanger(slot).exchange(value, duration)
  }
  
  def getSize = size

}
