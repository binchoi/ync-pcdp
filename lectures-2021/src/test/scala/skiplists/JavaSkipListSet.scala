package skiplists

import lists.ConcurrentSet

import java.util.concurrent.ConcurrentSkipListSet

/**
  * @author Ilya Sergey
  */
class JavaSkipListSet[T] extends ConcurrentSet[T] {

  private val set = new ConcurrentSkipListSet[T]()

  override def add(item: T) = set.add(item)

  override def remove(item: T) = set.remove(item)

  override def contains(item: T) = set.contains(item)
}
