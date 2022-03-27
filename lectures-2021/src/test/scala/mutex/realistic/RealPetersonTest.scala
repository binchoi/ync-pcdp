package mutex.realistic

import mutex.GenericLockTest

/**
  * @author Ilya Sergey
  */
class RealPetersonTest extends GenericLockTest {

  override val COUNT = 10000024

  override def makeLockInstance = new RealPetersonLock
}
