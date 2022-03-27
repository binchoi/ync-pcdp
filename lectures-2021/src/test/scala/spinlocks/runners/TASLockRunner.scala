package spinlocks.runners

import spinlocks.TASLock

/**
  * @author Ilya Sergey
  */
object TASLockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new TASLock
}
