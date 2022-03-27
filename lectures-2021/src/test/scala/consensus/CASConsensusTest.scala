package consensus

/**
  * @author Ilya Sergey
  */
class CASConsensusTest extends GenericConsensusTest {

  override val NUM_THREADS = 15

  override def mkConsensusProtocol = new CASConsensus
}
