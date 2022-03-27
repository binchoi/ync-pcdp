package mutex

/**
  * @author Ilya Sergey
  */
class FastPathTest extends GenericLockTest {

  // Feel free to change this number
  override val COUNT = 100

  override def makeLockInstance = new FastPath(new PetersonLock())
  // new PetersonLock()

}
