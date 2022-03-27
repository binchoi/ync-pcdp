package homework.problem5

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class ConcurrentQueue[T: ClassTag] {
  private var queue = new Array[T](0)

  def enq(elem: T) : Unit = this.synchronized{
    this.queue = queue ++ Array(elem)
  }

  def deq : Option[T] = {
    this.synchronized{
      if (this.queue.isEmpty) None else {
        val tmp = Some(this.queue(0))
        queue = queue.slice(1,this.queue.length)
        tmp
    }}
  }
  
  def size : Int =
    this.queue.length
  
  def isEmpty: Boolean=
    this.queue.length == 0

}
