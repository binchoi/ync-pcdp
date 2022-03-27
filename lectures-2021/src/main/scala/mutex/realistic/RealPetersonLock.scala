package mutex.realistic

import util.ThreadID

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock

/**
  * @author Ilya Sergey
  */
class RealPetersonLock extends Lock {

  private val flag: Array[AtomicBoolean] = Array.fill(2)(new AtomicBoolean(false))
  @volatile
  private var victim: Int = 0

  override def lock(): Unit = {
    val i = ThreadID.get
    val j = 1 - i
    flag(i).set(true)
    victim = i
    while (flag(j).get && victim == i) {
      // spin
    }
  }

  override def unlock(): Unit = {
    val i = ThreadID.get
    flag(i).set(false)
  }

  // Any class implementing Lock must provide these methods
  override def newCondition = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

  override def tryLock = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def lockInterruptibly(): Unit = {
    throw new UnsupportedOperationException
  }
}
