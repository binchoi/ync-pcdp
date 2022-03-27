package consensus

import java.util.concurrent.atomic.AtomicReference

/**
  * @author Ilya Sergey
  */
class Assign23[T](val init: T) {

  val r: Array[AtomicReference[T]] = Array.fill(3)(new AtomicReference(init))

  def assign(v0: T, v1: T, i0: Int, i1: Int): Unit = this.synchronized {
    r(i0).set(v0)
    r(i1).set(v1)
  }

  def read(i: Int): T = r(i).get()
}
