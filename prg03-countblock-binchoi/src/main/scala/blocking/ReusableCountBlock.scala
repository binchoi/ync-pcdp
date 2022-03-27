package blocking

import util.ThreadID

import java.util.concurrent.locks.ReentrantLock

/**
  * An implementation of CountBlock that allows
  * to use it for multiple stages. Once the counter reaches zero,
  * it should allow for counting down again.
  */
class ReusableCountBlock(numThreads: Int) {

  private var COUNTER: Int = numThreads // counter initialized to active threads being active
  private val lock = new ReentrantLock()
  private val counterZero = lock.newCondition()
  private val counterAlrZero = lock.newCondition()
  private var countDownThreads : List[Int] = List()

  def countDown(): Unit = {
    val threadID = ThreadID.get

    lock.lock()
    try {
      while (COUNTER == 0) { // if counter is already 0
        if (countDownThreads.contains(threadID)) {
          return None
        } else {
          counterAlrZero.await()
        }
      }

      COUNTER = COUNTER - 1
      countDownThreads = countDownThreads ++ List(threadID)
      if (COUNTER==0) {
        counterZero.signalAll()
      }
    } finally {
      lock.unlock()
    }
  }

  def getCount: Int = {
    lock.lock()
    try {
      COUNTER
    } finally {
      lock.unlock()
    }
  }

  def await(): Unit = {
    lock.lock()
    try {
      while (COUNTER!=0) {
        counterZero.await()
      }
    } finally {
      lock.unlock()
    }
  }
  /** Resets the block to its initial state, 
   * Releases all await-ing threads on this count block.  */
    
  def reset(): Unit = {
    lock.lock()
    try {
      COUNTER = numThreads
      counterZero.signalAll()
      counterAlrZero.signalAll()
    } finally {
      lock.unlock()
    }
  }

}

