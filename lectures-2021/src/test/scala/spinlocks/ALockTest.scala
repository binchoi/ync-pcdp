package spinlocks

/**
  * @author Ilya Sergey
  */
class ALockTest extends SpinLockTest {
  override def makeSpinLockInstance = new ALock(THREADS)
}
