package mutex

/**
  * @author Ilya Sergey
  */
class ModifiedPetersonTest extends GenericLockTest {

  // Feel free to change this number
  override val COUNT = 1000

  override def makeLockInstance = new ModifiedPetersonLock()
  // new PetersonLock()

}
