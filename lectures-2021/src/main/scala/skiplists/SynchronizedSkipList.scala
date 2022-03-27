package skiplists

import lists.ConcurrentSet

/**
  * @author Ilya Sergey
  */
class SynchronizedSkipList[T] extends ConcurrentSet[T] {
  val l = new SequentialSkipList[T]

  override def add(item: T) = this.synchronized{
    l.add(item)
  }

  override def remove(item: T) = this.synchronized{
    l.remove(item)
  }

  override def contains(item: T) = this.synchronized{
    l.contains(item)
  }
}
