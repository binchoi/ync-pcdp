package monitors.readwrite

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantLock}

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class FIFOReadWriteLock extends ReadWriteLock {

  private var readers = 0
  private var writer = false
  private var readAcquires, readReleases: Int = 0
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
        readAcquires += 1
      } finally {
        myLock.unlock()
      }
    }

    def unlock(): Unit = {
      myLock.lock()
      try {
        readReleases += 1
        if (readAcquires == readReleases) {
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

  protected class WriteLock extends Lock {
    def lock(): Unit = {
      myLock.lock()
      try {
        while (readAcquires != readReleases) try {
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
