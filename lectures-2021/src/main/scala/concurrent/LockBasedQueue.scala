package concurrent

import java.util.concurrent.locks.ReentrantLock
import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class LockBasedQueue[T: ClassTag](val capacity: Int) extends ConcurrentQueue[T] {
  
  private var head, tail: Int = 0 
  private val items = new Array[T](capacity)
  private val myLock = new ReentrantLock()
  
  def enq(x: T): Unit = {
    myLock.lock()
    try {
      if (tail - head == items.length) {
        throw FullException
      }
      items(tail % items.length) = x
      tail = tail + 1
    } finally {
      myLock.unlock()
    }
  }
  
  def deq() : T = {
    myLock.lock()
    try {
      if (tail == head) {
        throw EmptyException
      }
      val x = items(head % items.length)
      head = head + 1
      x
    } finally {
      myLock.unlock()
    }
  }
  
}

