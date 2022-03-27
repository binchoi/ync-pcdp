package stacks

/**
  * @author Ilya Sergey
  */
class EliminationBackoffStackTests extends ConcurrentStackTests {
  override def mkStack(capacity: Int) = new EliminationBackoffStack[Int]
}
