package consensus

import consensus.rmw.RMWRegister
import util.ThreadID

/**
  * @author Ilya Sergey
  */
class CASConsensus extends ConsensusProtocol[Int] {
  private val FIRST = -1
  private val r = new RMWRegister(FIRST)

  override def decide(value: Int) = {
    propose(value)
    val i = ThreadID.get
    if (r.compareAndSet(FIRST, i)) {
      proposed(i).get() // I won
    } else {
      proposed(r.read).get()
    }
  }
}
