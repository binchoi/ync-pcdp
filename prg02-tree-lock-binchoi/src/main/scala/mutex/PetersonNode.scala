package mutex

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

/**
  * @author Ilya Sergey
  */
class PetersonNode {

  private val flag: Array[AtomicBoolean] = Array.fill(2)(new AtomicBoolean(false))
  @volatile private var victim: Int = -1

  def lock(threadID: Int): Unit = {
    val i = threadID % 2
    val j = 1 - i
    flag(i).set(true)
    victim = i
    while (flag(j).get && victim == i) {}
  }

  def unlock(threadID: Int): Unit = {
    val i = threadID % 2
    flag(i).set(false)
  }

}
