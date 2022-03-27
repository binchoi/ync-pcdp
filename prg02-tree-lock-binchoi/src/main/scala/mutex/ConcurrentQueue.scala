package mutex

import scala.reflect.ClassTag
import mutex.TreeLock

class ConcurrentQueue[T: ClassTag](myTreeLock: TreeLock) {
  private var queue = new Array[T](0)

  def enq(elem: T) : Unit = {
    myTreeLock.lock()
    this.queue = queue ++ Array(elem)
    myTreeLock.unlock()
  }

  def deq : Option[T] = {
    myTreeLock.lock()
    var tmp = None
    if (this.queue.isEmpty) {None} else {
      val tmp = Some(this.queue(0))
      queue = queue.slice(1,this.queue.length)
    }
    myTreeLock.unlock()
    tmp
  }

  def size : Int = {
    myTreeLock.lock()
    val tmp = this.queue.length
    myTreeLock.unlock()
    tmp
  }

  def isEmpty: Boolean= {
    myTreeLock.lock()
    val res = (this.queue.length == 0)
    myTreeLock.unlock()
    res
  }
}