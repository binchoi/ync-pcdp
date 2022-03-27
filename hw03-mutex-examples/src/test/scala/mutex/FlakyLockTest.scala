package mutex

/**
  * @author Ilya Sergey
  */
class FlakyLockTest extends GenericLockTest {

  // Feel free to change this number
  override val COUNT = 100

  override def makeLockInstance = new FlakyLock

}
