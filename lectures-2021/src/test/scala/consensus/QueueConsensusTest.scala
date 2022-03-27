package consensus

/**
  * @author Ilya Sergey
  */
class QueueConsensusTest extends GenericConsensusTest {

  override val NUM_THREADS = 2

  override def mkConsensusProtocol = new QueueConsensus
}
