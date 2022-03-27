package monitors.readwrite

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantLock}

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class SimpleReadWriteLock extends ReadWriteLock {

  private var readers = 0
  private var writer = false
  private val myLock = new ReentrantLock
  private val myReadLock = new ReadLock
  private val myWriteLock = new WriteLock
  private var condition = myLock.newCondition

  def readLock: Lock = myReadLock

  def writeLock: Lock = myWriteLock

  class ReadLock extends Lock {
    def lock(): Unit = {
      myLock.lock()
      try {
        while (writer) try {
          condition.await()
        } catch {
          case e: InterruptedException =>
        }
        readers += 1
      } finally {
        myLock.unlock()
      }
    }

    def unlock(): Unit = {
      myLock.lock()
      try {
        readers -= 1
        if (readers == 0) {
          condition.signalAll()
        }
      } finally myLock.unlock()
    }

    @throws[InterruptedException]
    def lockInterruptibly(): Unit = throw new UnsupportedOperationException

    def tryLock = throw new UnsupportedOperationException

    @throws[InterruptedException]
    def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

    def newCondition = throw new UnsupportedOperationException
  }
  
  // TODO: Can you point out any liveness issues with this WriteLock?

  protected class WriteLock extends Lock {
    def lock(): Unit = {
      myLock.lock()
      try {
        while (readers > 0) try {
          condition.await()
        } catch {
          case e: InterruptedException =>
        }
        writer = true
      } finally myLock.unlock()
    }

    def unlock(): Unit = {
      myLock.lock()
      try {
        writer = false
        condition.signalAll()
      } finally {
        myLock.unlock()
      }
    }

    @throws[InterruptedException]
    def lockInterruptibly(): Unit = throw new UnsupportedOperationException

    def tryLock = throw new UnsupportedOperationException

    @throws[InterruptedException]
    def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

    def newCondition = throw new UnsupportedOperationException
  }

  @throws[InterruptedException]
  def lockInterruptibly(): Unit = throw new UnsupportedOperationException

  def tryLock = throw new UnsupportedOperationException

  @throws[InterruptedException]
  def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

  def newCondition = throw new UnsupportedOperationException

}
