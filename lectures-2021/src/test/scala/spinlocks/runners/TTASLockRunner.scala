package spinlocks.runners

import spinlocks.TTASLock

/**
  * @author Ilya Sergey
  */
object TTASLockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new TTASLock
}
