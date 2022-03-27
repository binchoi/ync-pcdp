package spinlocks

import java.util.concurrent.atomic.AtomicInteger

/**
  * @author Ilya Sergey
  */
class ALock(private val capacity: Int) extends SpinLock {

  // thread-local variable
  private val mySlotIndex = new ThreadLocal[Integer]() {
    override protected def initialValue = 0
  }
  val size = capacity
  val tail = new AtomicInteger(0)
  @volatile var flag = new Array[Boolean](size)
  flag(0) = true

  def lock(): Unit = {
    val slot = tail.getAndIncrement % size
    mySlotIndex.set(slot)
    while (!flag(slot)) {} // spin
  }

  def unlock(): Unit = {
    val slot = mySlotIndex.get
    flag(slot) = false
    flag = flag
    flag((slot + 1) % size) = true
  }
}
