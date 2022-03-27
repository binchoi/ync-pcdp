package consensus

import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
 * @author Ilya Sergey
 */
trait GenericConsensusTest extends FunSpec with Matchers {
  val NUM_THREADS = 2

  def getInput = 1 to NUM_THREADS

  def mkConsensusProtocol: ConsensusProtocol[Int]

  describe(s"A consensus protocol for ${this.getClass.getName}") {
    it("should be correct ") {
      ThreadID.reset()
      val cp = mkConsensusProtocol

      val threads = for (i <- getInput) yield new Proposer(i, cp)

      // Start all threads
      threads.foreach(_.start())
      // Wait for all threads to join
      threads.foreach(_.join())

      val decisionOpts = threads.map(_.getDecision)

      // All decided something
      assert(decisionOpts.forall(_.isDefined), "Consensus should take place")

      val decisions = decisionOpts.map(_.get)
      val first = decisions.head
      assert(decisions.forall(_ == first), "All decisions should be the same")
      println(s"Decisions: [${decisions.mkString(", ")}]")

    }
  }


  class Proposer(value: Int, hat: ConsensusProtocol[Int]) extends Thread {

    private var decided: Option[Int] = None

    override def run(): Unit = {
      Thread.sleep(0, 5)
      val result = hat.decide(value)
      decided = Some(result)
    }

    def getDecision = decided
  }

}
