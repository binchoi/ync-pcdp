package stacks

/**
  * @author Ilya Sergey
  */
class SynchronizedStackTests extends ConcurrentStackTests {
  override def mkStack(capacity: Int) = new SynchronizedStack[Int]
}
