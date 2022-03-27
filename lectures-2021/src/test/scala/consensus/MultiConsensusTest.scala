package consensus

/**
  * @author Ilya Sergey
  */
class MultiConsensusTest extends GenericConsensusTest {

  override val NUM_THREADS = 2

  override def mkConsensusProtocol = new MultiConsensus[Int]
}

