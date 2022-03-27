package consensus

import util.ThreadID

import java.util.concurrent.ConcurrentLinkedQueue
import scala.reflect.ClassTag

/**
  * @author Maurice Herlihy, Ilya Sergey
  */
class QueueConsensus[T: ClassTag] extends ConsensusProtocol[T] {
  private val RED = 0   // Red ball
  private val BLACK = 1 // Black ball

  val queue = new ConcurrentLinkedQueue[Int]
  // Initialise
  queue.add(RED)
  queue.add(BLACK)

  override def decide(value: T) = {
    propose(value)
    val status = try {
      queue.remove()
    } catch {
      case _: NoSuchElementException => BLACK 
    }
    val i = ThreadID.get
    if (status == RED) {
      proposed(i).get()
    } else {
      proposed(1 - i).get()
    }

  }
}
