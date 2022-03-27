package monitors

import monitors.readwrite.SimpleReadWriteLock

/**
  * @author Ilya Sergey
  */
class SimpleRWLockTest extends ReadWriteLockTest {

  val lock = new SimpleReadWriteLock

}
