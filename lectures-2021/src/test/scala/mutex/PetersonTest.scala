package mutex

/**
  * @author Ilya Sergey
  */
class PetersonTest extends GenericLockTest {
  override def makeLockInstance = new PetersonLock
}
