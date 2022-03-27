package spinlocks.runners

import spinlocks.CLHLock

/**
  * @author Ilya Sergey
  */
object CLHLockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new CLHLock
}
