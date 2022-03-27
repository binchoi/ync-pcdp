package mutex

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

import util.ThreadID


/**
  * This is the class you will be implementing to guard the critical section
  * from the merciless horde of n threads you'll be unleashing to compete for  
  * an access to it. 
  */
class TreeLock(numThreads: Int) extends Lock {

  var temp_n: Int = numThreads
  var levels = new Array[Int](0)
  while (temp_n != 1) {
    if (temp_n%2 == 1) {
      temp_n = (temp_n+1)/2
    } else {
      temp_n = temp_n/2
    }
    levels = levels ++ Array(temp_n)
  }

  val height: Int = levels.length

  val pNode_arr = new Array[Array[PetersonNode]](height)
  for (i <- levels.indices) {
    pNode_arr(i) = Array.fill(levels(i))(new PetersonNode())
  }
  private var pNodesToUnlock = new Array[(PetersonNode, Int)](height)

  override def lock(): Unit = {
    val threadID = ThreadID.get
    var pNodesToUnlockCand = new Array[(PetersonNode, Int)](0) //////try this change to ++

    var temp_id = threadID
    var counter = 0

    for (rep <- 1 to height) {
      var childStatus: Int = -1 // initialize
      var h1 = (temp_id / 2) //floor division
      if (h1 == (temp_id.toFloat / 2)) { // if floor int is same as float
        childStatus = 0
      } else {
        childStatus = 1
      }
      pNode_arr(counter)(h1).lock(childStatus) // left/right child is 0/1
      pNodesToUnlockCand =Array((pNode_arr(counter)(h1), childStatus)) ++ pNodesToUnlockCand
      counter += 1
      temp_id = h1
      }
    pNodesToUnlock = pNodesToUnlockCand
  }

  override def unlock(): Unit = {
    val threadID = ThreadID.get
    val pNodesToUnlock_copy = pNodesToUnlock
    for ((pNode, childStatus) <- pNodesToUnlock_copy) {
      (pNode).unlock(childStatus)
    }
  }

  // The compiler wants these declarations since TreeLock implements Java Lock
  override def newCondition = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def tryLock(time: Long, unit: TimeUnit) = throw new UnsupportedOperationException

  override def tryLock = throw new UnsupportedOperationException

  @throws[InterruptedException]
  override def lockInterruptibly(): Unit = throw new UnsupportedOperationException
}

