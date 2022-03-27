package queues

import concurrent.{ConcurrentQueue, EmptyException}

/**
  * @author Ilya Sergey
  */
class SynchronizedQueue[T] extends ConcurrentQueue[T] {

  val q = new collection.mutable.Queue[T]

  override def enq(x: T): Unit = this.synchronized {
    q.enqueue(x)
  }

  override def deq() = this.synchronized {
    try {
      q.dequeue()
    } catch {
      case _: NoSuchElementException => throw EmptyException
    }
  }

  override def toListThreadSafe = this.synchronized {
    q.toList
  }
}
