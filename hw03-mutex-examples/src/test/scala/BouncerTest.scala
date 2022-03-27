import Direction._
import org.scalatest.{FunSpec, Matchers}
import util.ThreadID

/**
  * @author Ilya Sergey
  */
class BouncerTest extends FunSpec with Matchers {
  val N = 20

  class BouncerThread(b: Bouncer) extends Thread {
    var result: Option[Value] = None

    override def run() = {
      result = Some(b.visit())
    }
  }


  describe(s"Bouncer class") {
    it("should satisfy some statements") {
      ThreadID.reset()
      val bouncer = new Bouncer
      
      // Lazily fill the array with many threads visiting the bouncer
      val threads = Array.fill(N)(new BouncerThread(bouncer))

      // Start all threads
      for (i <- 0 until N) {
        threads(i).start()
      }

      // Wait for all threads to join
      for (i <- 0 until N) {
        threads(i).join()
      }
      
      // Get all results from the corresponding "Some" wrappers
      // Underscore "_" is a shortcut for a one-argument function
      // equivalently `threads.toList.map(x => x.result.get)`
      val results = threads.toList.map(_.result.get) 
      
      // for (r <- results) println(r)

      // At most one STOP amongst the results
      assert(results.count(_ == STOP) <= 1)

      // At most (N - 1) RIGHT
      assert(results.count(_ == RIGHT) < N)

      // At most (N - 1) DOWN
      assert(results.count(_ == DOWN) < N)
      
    }
  }


}
