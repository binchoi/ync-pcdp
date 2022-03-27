package queues

/**
  * @author Ilya Sergey
  */
class UnboundedQueueTest extends ConcurrentQueueTests {

  override def mkQueue(capacity: Int) = new UnboundedQueue[Int]

}
