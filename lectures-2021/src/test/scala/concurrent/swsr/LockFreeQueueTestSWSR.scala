package concurrent.swsr

import concurrent.LockFreeQueue

/**
  * @author Ilya Sergey
  */
class LockFreeQueueTestSWSR extends ConcurrentQueueTestSWSR {
  override def mkQueue(capacity: Int) = new LockFreeQueue[Int](capacity)
}

