/*
 * @author Ilya Sergey
 * 
 */
package mutex.realistic

import util.ThreadID

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.concurrent.locks.Lock


class RealBakeryLock(val threads: Int) extends Lock {

  @volatile
  private var label: Array[AtomicInteger] =
    Array.fill(threads)(new AtomicInteger(0))
  @volatile
  private var flag: Array[AtomicBoolean] =
    Array.fill(threads)(new AtomicBoolean(false))

  override def lock(): Unit = {
    val i = ThreadID.get
    flag(i).set(true)
    flag = flag
    label(i) = new AtomicInteger(findMaximumElement(label) + 1)
    label = label
    for (k <- 0 until threads if k != i) {
      while (
        flag(k).get() &&
          (label(k).get() < label(i).get() ||
            ((label(k).get() == label(i).get()) && k < i))) {
        //spin wait
      }
    }
  }

  override def unlock(): Unit = {
    flag(ThreadID.get).set(false)
    flag = flag
  }

  private def findMaximumElement(elementArray: Array[AtomicInteger]) = {
    var maxValue = 0
    for (i <- elementArray.indices; e = elementArray(i).get) {
      if (e > maxValue) maxValue = e
    }
    maxValue
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
