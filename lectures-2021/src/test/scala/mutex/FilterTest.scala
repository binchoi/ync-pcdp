package mutex

/**
  * @author Ilya Sergey
  */
class FilterTest extends GenericLockTest {
  override def makeLockInstance = new FilterLock(THREADS)
}

