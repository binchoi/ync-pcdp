package spinlocks

import java.util.concurrent.atomic.AtomicInteger

/**
  * @author Ilya Sergey
  */
class TicketedLock extends SpinLock {

  val next = new AtomicInteger(0)
  val owner = new AtomicInteger(0)
  
  override def lock() = {
    val ticket = next.getAndIncrement()
    while (ticket != owner.get()) {} // spin
  }

  override def unlock() = {
    //    if (owner.get() % 10000 == 0) {
    //      println(owner.get())
    //    }

    owner.getAndIncrement()
  }
}
