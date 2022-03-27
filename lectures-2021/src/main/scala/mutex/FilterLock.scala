/*
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 *
 * Scala version by Ilya Sergey
 * 
 */
package mutex

import util.ThreadID

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

/**
  * @author Ilya Sergey
  */
class FilterLock(threads: Int) extends Lock {
  private val size: Int = threads
  private val level: Array[Int] = new Array(threads)
  private val victim: Array[Int] = new Array(threads - 1)


  override def lock(): Unit = {
    val me = ThreadID.get
    for (i <- 0 until size - 1) {
      level(me) = i
      victim(i) = me
      // spin while conflicts exist
      while (sameOrHigher(me, i) && victim(i) == me) {}
    }
  }

  private def sameOrHigher(me: Int, myLevel: Int): Boolean = {
    for (id <- 0 until size) {
      if (id != me && level(id) >= myLevel) {
        return true
      }
    }
    false
  }

  override def unlock(): Unit = {
    level(ThreadID.get) = -1
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
