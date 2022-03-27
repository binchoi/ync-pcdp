package spinlocks

/**
  * @author Ilya Sergey
  */
class BackoffLockTest extends SpinLockTest {
  override def makeSpinLockInstance = new BackoffLock
}
