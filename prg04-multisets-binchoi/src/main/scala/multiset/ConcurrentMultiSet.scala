package multiset

/**
  * @author Ilya Sergey
  */
trait ConcurrentMultiSet[T] {

  def add(item: T): Boolean

  def remove(item: T): Boolean

  def contains(item: T): Boolean

  def count(item: T): Int

}
