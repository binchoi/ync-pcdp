package spinlocks

/**
  * @author Ilya Sergey
  */
class TTASLockTest extends SpinLockTest {
  override def makeSpinLockInstance = new TTASLock
}
