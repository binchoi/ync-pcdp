package concurrent

import java.util.concurrent.atomic.{AtomicInteger, AtomicReferenceArray}

import util.ThreadID

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class IQueue[T: ClassTag] {

  val head = new AtomicInteger(0)
  val tail = new AtomicInteger(0)
  @volatile
  var items = new AtomicReferenceArray[T](2048)

  def enq(x: T): Unit = {
    val i = ThreadID.get
    // println(s"$i:enq($x)")
    var slot = -1
    do {
      slot = tail.get()
    } while (!tail.compareAndSet(slot, slot + 1))
    items.set(slot, x)
    items = items // Never mind this: required to sync a reference to an array
    // println(s"$i:enq:void")
  }

  def deq(): Option[T] = {
    val i = ThreadID.get
    // println(s"$i:deq()")
    var value: Option[T] = None
    var slot = -1
    do {
      slot = head.get()
      val tmp = items.get(slot)
      if (tmp == null) {
        // println(s"$i:deq:None")
        return None
      } else {
        value = Some(tmp)
      }
    } while (!head.compareAndSet(slot, slot + 1))
    val valStr = value match {
      case None => "None"
      case Some(x) => s"Some($x)"
    }
    // println(s"$i:deq:$valStr")
    value
  }

}
