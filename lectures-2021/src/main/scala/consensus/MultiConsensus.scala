package consensus

import util.ThreadID

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class MultiConsensus[T: ClassTag] extends ConsensusProtocol[T] {
  private val NULL = -1
  private val assign23 = new Assign23(NULL)

  override def decide(value: T) = {
    propose(value)
    val i = ThreadID.get
    // double assignment
    assign23.assign(i, i, i, i + 1)
    val other = assign23.read((i + 2) % 3)
    if (other == NULL || other == assign23.read(1)) {
      proposed(i).get() // I win
    } else {
      proposed(1 - i).get() // I lose
    }
  }
}
