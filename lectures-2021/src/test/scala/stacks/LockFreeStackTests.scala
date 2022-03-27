package stacks

/**
  * @author Ilya Sergey
  */
class LockFreeStackTests extends ConcurrentStackTests {
  override def mkStack(capacity: Int) = new LockFreeStack[Int]
}
