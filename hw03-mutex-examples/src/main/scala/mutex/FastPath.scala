package mutex

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

import util.ThreadID

class FastPath(myLock: Lock) extends Lock {
  
  @volatile
  private var x, y: Int = -1
  
  override def lock() = {
    val i = ThreadID.get
    x = i                // I'm here!
    while (y != - 1) {}  // Is the lock free?
    y = i                // Me again?
    if (x != i) {        // Am I still here? 
      myLock.lock()      // Take the regular lock (slow path)
    }
                         // Take the fast path
  }

  override def unlock() = {
    y = -1
    myLock.unlock()
  }

  
  
  // Any class implementing Lock must provide these methods
  override def newCondition = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

  override def tryLock = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def lockInterruptibly(): Unit = throw new UnsupportedOperationException
}
