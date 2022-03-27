package blocking

import util.ThreadID

import java.util.concurrent.locks.ReentrantLock


/**
  * This is the class you will be implementing to guard 
  * bring the order into the world of concurrent chaos.
  */
class CountBlock(numThreads: Int) {

  private var COUNTER: Int = numThreads // counter initialized to active threads being active
  private val lock = new ReentrantLock()
  private val counterZero = lock.newCondition()
  private val counterAlrZero = lock.newCondition()
  private var countDownThreads : List[Int] = List()

  def countDown(): Unit = {
    val threadID = ThreadID.get

    lock.lock()
    try {
      if (COUNTER == 0) { // if counter is already 0
        if (countDownThreads.contains(threadID)) {
          None
        } else {
          counterAlrZero.await()
        }
      }

      COUNTER = COUNTER - 1
      countDownThreads = countDownThreads ++ List(threadID)
      if (COUNTER==0) {
        counterZero.signalAll() // Free when counter is 0
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
      while (COUNTER != 0) {
        counterZero.await()
      }
    } finally {
      lock.unlock()
    }
  }


}

