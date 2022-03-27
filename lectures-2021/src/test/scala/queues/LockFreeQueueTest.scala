package queues

/**
  * @author Ilya Sergey
  */
class LockFreeQueueTest extends  ConcurrentQueueTests {
  
  override def mkQueue(capacity: Int) = new LockFreeQueue[Int]

}
