package spinlocks.runners

import spinlocks.BackoffLock

/**
  * @author Ilya Sergey
  */
object BackoffLockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new BackoffLock
}
