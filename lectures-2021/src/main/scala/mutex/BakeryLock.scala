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


class BakeryLock(val threads: Int) extends Lock {

  private val label: Array[Label] = Array.fill(threads)(new Label())
  private val flag: Array[Boolean] = new Array(threads)

  override def lock(): Unit = {
    val me = ThreadID.get
    flag(me) = true
    val max = label.foldLeft(0)((c, l) => Math.max(c, l.counter))
    label(me) = new Label(max + 1)
    while (conflict(me)) {} // spin
  }

  override def unlock(): Unit = {
    flag(ThreadID.get) = false
  }

  private def conflict(me: Int): Boolean = label.indices.exists(i =>
    i != me && flag(i) && label(me).compareTo(label(i)) < 0
  )

  class Label(val counter: Int = 0) extends Comparable[Label] {
    val id: Long = ThreadID.get

    override def compareTo(other: Label) = {
      if (this.counter < other.counter ||
        (this.counter == other.counter && this.id < other.id)) -1
      else if (this.counter > other.counter) 1
      else 0
    }
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
