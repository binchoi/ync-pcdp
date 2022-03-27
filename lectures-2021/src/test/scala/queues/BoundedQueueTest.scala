package queues

/**
  * @author Ilya Sergey
  */
class BoundedQueueTest extends  ConcurrentQueueTests {
  
  override def mkQueue(capacity: Int) = new BoundedQueue[Int](capacity)

}
