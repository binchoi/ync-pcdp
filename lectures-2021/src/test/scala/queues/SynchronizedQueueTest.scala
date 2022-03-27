package queues

/**
  * @author Ilya Sergey
  */
class SynchronizedQueueTest  extends ConcurrentQueueTests {
  override def mkQueue(capacity: Int) = new SynchronizedQueue[Int]
}
