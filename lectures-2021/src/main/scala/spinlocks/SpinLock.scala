package spinlocks

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

/**
  * @author Ilya Sergey
  */
trait SpinLock extends Lock {

  // Any class implementing Lock must provide these methods
  override def newCondition = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

  override def tryLock = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def lockInterruptibly(): Unit = throw new UnsupportedOperationException

}
