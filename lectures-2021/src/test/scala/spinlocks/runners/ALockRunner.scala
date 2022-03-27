package spinlocks.runners

import spinlocks.ALock

/**
  * @author Ilya Sergey
  */
object ALockRunner extends SpinLockBenchmark {
  override def makeSpinLockInstance = new ALock(THREADS)
}
