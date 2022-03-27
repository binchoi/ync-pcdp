package homework.problem4

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class SequentialStack[T: ClassTag] {
  private var stk = new Array[T](0)

  def push(elem: T): Unit =
    this.stk = Array(elem) ++ this.stk

  def pop: Option[T] = {
    if (this.stk.isEmpty) None else {
      val tmp = Some (this.stk(0))
      stk = stk.slice(1,this.stk.length)
      tmp
    }
  }

  def peek: Option[T] = {
    if (this.isEmpty) None else Some (this.stk(0))
  }

  def size: Int =
    this.stk.length

  def isEmpty: Boolean = // is it okay that I rearranged
    this.stk.length == 0

  def get_stk: Array[T] =
    this.stk

}
