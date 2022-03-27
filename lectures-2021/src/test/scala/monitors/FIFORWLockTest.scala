package monitors

import monitors.readwrite.FIFOReadWriteLock

/**
  * @author Ilya Sergey
  */
class FIFORWLockTest extends ReadWriteLockTest {

  val lock = new FIFOReadWriteLock

}
