package concurrent

/**
  * @author Ilya Sergey
  */
trait ConcurrentQueue[T] {

  def enq(x: T): Unit

  def deq(): T

  def toListUnsafe: List[T] = {
    var l : List[T] = Nil
    try {
      while (true) {
        l = l ++ List(deq())
      }
      l // doesn't happen
    } catch {
      case EmptyException => l
    }
  }

  def toListThreadSafe: List[T] = ??? // Throws exception
  
}

// Exceptions
case object FullException extends Exception
case object EmptyException extends Exception
