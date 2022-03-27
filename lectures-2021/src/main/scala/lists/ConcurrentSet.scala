package lists

import java.util.concurrent.atomic.AtomicInteger

/**
  * @author Ilya Sergey
  */
trait ConcurrentSet[T] {

  def add(item: T): Boolean

  def remove(item: T): Boolean

  def contains(item: T): Boolean

  protected val checkFailCounter = new AtomicInteger(0)

  def checkFailures = checkFailCounter.get()

}
