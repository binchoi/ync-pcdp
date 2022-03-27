package homework.problem5

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class ConcurrentStack[T: ClassTag] {
  private var stk = new Array[T](0)

  def push(elem: T): Unit =
    this.synchronized{
      this.stk = Array(elem) ++ stk
    }

  def pop: Option[T] = {
    this.synchronized{
      if (this.stk.isEmpty) None else {
        val tmp: Option[T] = Some(this.stk(0))
        stk = stk.slice(1,this.stk.length)
        tmp
      }
    }
  }

  def peek: Option[T] = {
    if (this.isEmpty) None else Some(this.stk(0))
  }

  def size: Int =
    this.stk.length

  def isEmpty: Boolean =
    this.stk.length == 0

}
