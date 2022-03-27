package spinlocks

/**
  * @author Ilya Sergey
  */
class TicketLockTest extends SpinLockTest {
  override def makeSpinLockInstance = new TicketedLock
}
