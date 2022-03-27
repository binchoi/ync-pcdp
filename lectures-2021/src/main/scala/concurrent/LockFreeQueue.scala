package concurrent

import util.ThreadID

import scala.reflect.ClassTag

/**
 * @author Ilya Sergey
 */
class LockFreeQueue[T: ClassTag](val capacity: Int) extends ConcurrentQueue[T] {

  @volatile
  private var head, tail: Int = 0
  private val items = new Array[T](capacity)

  def enq(x: T): Unit = {
    //    println(s"Thread ${ThreadID.get} enq($x)")
    if (tail - head == items.length) {
      //      println(s"Thread ${ThreadID.get}: enq:Fail")
      throw FullException
    }
    items(tail % items.length) = x
    tail = tail + 1
    //    println(s"Thread ${ThreadID.get} enq:void")
  }

  def deq(): T = {
    //    println(s"  Thread ${ThreadID.get} deq()")
    if (tail == head) {
      //      println(s"  Thread ${ThreadID.get}: deq:Nothing")
      throw EmptyException
    }
    val x = items(head % items.length)
    head = head + 1
    //    println(s"  Thread ${ThreadID.get}: deq($x)")
    x
  }
}
