package mutex

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

import util.ThreadID

/**
  * @author Ilya Sergey
  */
class FlakyLock extends Lock {

  @volatile private var turn: Int = -1
  @volatile private var busy: Boolean = false

  override def lock(): Unit = {
    val me = ThreadID.get
    do {
      do {
        turn = me
      } while (busy)
      busy = true
    } while (turn != me)
  }

  override def unlock(): Unit = {
    busy = false
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
