package stacks

import concurrent.EmptyException

/**
  * @author Ilya Sergey
  */
class SynchronizedStack[T] extends ConcurrentStack[T] {
  
  private var stack : List[T] = Nil
  
  override def push(x: T): Unit = this.synchronized{
    stack = x :: stack
  }

  override def pop(): T = this.synchronized{
    stack match {
      case h :: t =>
        stack = t
        h
      case _ => throw EmptyException
    }
  }

  override def toListThreadUnsafe = this.synchronized{
    stack
  }
}
