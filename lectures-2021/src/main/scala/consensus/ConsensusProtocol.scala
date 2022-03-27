package consensus

import util.ThreadID

import java.util.concurrent.atomic.AtomicReference
import scala.reflect.ClassTag

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
abstract class ConsensusProtocol[T: ClassTag] extends Consensus[T] {

  private val THREADS_NUM = 256
  @volatile
  protected var proposed = Array.fill(THREADS_NUM)(new AtomicReference[T]())

  // Announce my input to other threads
  protected def propose(value: T): Unit = {
    val i = ThreadID.get

    // Exclude threads with very large thread IDs
    if (i < THREADS_NUM) {
      proposed(i).set(value)
      proposed = proposed
    }
  }

  // Figure out which thread was first
  def decide(value: T): T

}
