package spinlocks

/**
  * @author Ilya Sergey
  */
class CLHLockTest extends SpinLockTest {
  override def makeSpinLockInstance = new CLHLock
}
