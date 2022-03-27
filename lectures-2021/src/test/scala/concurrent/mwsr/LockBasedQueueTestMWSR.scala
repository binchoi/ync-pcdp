package concurrent.mwsr

import concurrent.LockBasedQueue

/**
  * @author Ilya Sergey
  */
class LockBasedQueueTestMWSR extends ConcurrentQueueTestMWSR {
  override def mkQueue(capacity: Int) = new LockBasedQueue[Int](capacity)
}
