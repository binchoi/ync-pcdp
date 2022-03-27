package mutex

/**
  * @author Ilya Sergey
  */
class BakeryTest extends GenericLockTest {

  override def makeLockInstance = new BakeryLock(THREADS)
}

