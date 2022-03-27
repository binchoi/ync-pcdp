package stacks

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicStampedReference
import scala.concurrent.TimeoutException
import scala.concurrent.duration.TimeUnit

/**
  * @author Ilya Sergey
  */
class LockFreeExchanger[T] {

  val slot = new AtomicStampedReference[T](null.asInstanceOf[T], 0)

  // Different statuses
  val EMPTY = 0
  val WAITING = 1
  val BUSY = 2

  @throws[TimeoutException]
  def exchange(myItem: T, 
               timeout: Long, 
               unit: TimeUnit = TimeUnit.MILLISECONDS): T = {
    
    val timeBound = System.nanoTime() + unit.toNanos(timeout)
    val stampholder = Array(EMPTY)
    while (true) {
      if (System.nanoTime() > timeBound) {
        throw new TimeoutException
      }
      var yrItem = slot.get(stampholder)
      val stamp = stampholder(0)
      stamp match {
        case EMPTY =>
          if (slot.compareAndSet(yrItem, myItem, EMPTY, WAITING)) {
            while (System.nanoTime() < timeBound) {
              yrItem = slot.get(stampholder)
              if (stampholder(0) == BUSY) {
                slot.set(null.asInstanceOf[T], EMPTY)
                return yrItem
              }
            }
            if (slot.compareAndSet(myItem, null.asInstanceOf[T], WAITING, EMPTY)) {
              throw new TimeoutException
            } else {
              yrItem = slot.get(stampholder)
              slot.set(null.asInstanceOf[T], EMPTY)
              return yrItem
            }
          }
        case WAITING =>
          if (slot.compareAndSet(yrItem, myItem, WAITING, BUSY)) {
            return yrItem
          }
        case BUSY =>
        case x => 
          throw new Exception("Cannot happen")
      }
    }
    throw new Exception("Cannot happen")
  }
  
}
