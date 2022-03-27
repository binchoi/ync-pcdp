/*
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 * 
 * Scala version by Ilya Sergey
 */
package mutex

import util.ThreadID

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

class LockTwo extends Lock {

  @volatile
  private var victim: Int = 0

  override def lock(): Unit = {
    val i = ThreadID.get
    victim = i             // let the other go first
    while (victim == i) {
      // spin
    }
  }

  override def unlock(): Unit = {}

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
