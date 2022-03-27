package concurrent.swsr

import concurrent.LockBasedQueue

/**
  * @author Ilya Sergey
  */
class LockBasedQueueTestSWSR extends ConcurrentQueueTestSWSR {
  override def mkQueue(capacity: Int) = new LockBasedQueue[Int](capacity)
}
