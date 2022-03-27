package spinlocks.runners

import spinlocks.{ALock, TicketedLock}

/**
  * @author Ilya Sergey
  */
object TicketedLockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new TicketedLock
}
