package stacks

import concurrent.EmptyException

/**
  * @author Ilya Sergey
  */
trait ConcurrentStack[T] {
  
  def push(x: T): Unit

  /**
    * @throws EmptyException 
    */
  def pop(): T

  def toListThreadUnsafe: List[T]
}

