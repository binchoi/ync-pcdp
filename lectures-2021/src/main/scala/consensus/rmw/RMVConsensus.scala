package consensus.rmw





/*
import consensus.ConsensusProtocol
import util.ThreadID

/**
  * @author Ilya Sergey
  */
class RMVConsensus(v: Int) extends ConsensusProtocol[Int] {

  val r: RMWRegister = new RMWRegister(v)
  
  override def decide(value: Int) = {
    propose(value)
    val i = ThreadID.get
    if (r.getAndMumble == v) {
      proposed(i).get()
    } else {
      proposed(1 - i).get()
    }
  }
}
*/
