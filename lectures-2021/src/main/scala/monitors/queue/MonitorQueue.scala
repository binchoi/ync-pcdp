package monitors.queue

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class MonitorQueue[T: ClassTag](val capacity: Int) {

  import java.util.concurrent.locks.ReentrantLock

  val lock = new ReentrantLock
  val notFull = lock.newCondition
  val notEmpty = lock.newCondition
  val items = new Array[T](capacity)
  var tail = 0
  var head = 0
  var count = 0

  @throws[InterruptedException]
  def enq(x: T): Unit = {
    lock.lock()
    try {
      while (count == items.length) {
        notFull.await()
      }
      items(tail) = x
      tail += 1
      if (tail == items.length) {
        tail = 0
      }
      count += 1
      // TODO: Can we do the following instead?
      // if (count == 1) notEmpty.signal()
      
      notEmpty.signal()
      
    } finally lock.unlock()
  }

  @throws[InterruptedException]
  def deq(): T = {
    lock.lock()
    try {
      while (count == 0) notEmpty.await()
      val x = items(head)
      head += 1
      if (head == items.length) {
        head = 0
      }
      count -= 1
      notFull.signal()
      x
    } finally lock.unlock()
  }
}
