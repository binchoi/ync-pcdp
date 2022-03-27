package spinlocks

/**
  * @author Ilya Sergey
  */
class TASLockTest extends SpinLockTest {
  override def makeSpinLockInstance = new TASLock
}
