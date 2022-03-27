package futures

import org.scalatest.AsyncFlatSpec

import scala.concurrent.Future

/**
  * Example 12
  */
class ScalaFuturesTests extends AsyncFlatSpec {

  // Testing futures

  def futureFactorial(n: Int): Future[Int] = Future {
    (1 to n).product
  }

  behavior of "futureFactorial"

  it should "eventually compute a factorial" in {
    val futureFact: Future[Int] = futureFactorial(6)
    // You can map assertions onto a Future, then return
    // the resulting Future[Assertion] to ScalaTest:
    for (res <- futureFact) yield {
      assert(res == 720)
    }
    // Alternative notation
    // futureFact map { f => assert(f == 3) }
  }

  behavior of "getAllBlacklisted"
  it should "correctly process .gitignore" in {
    val allIgnored = BlacklistedFiles.getAllBlacklisted(".gitignore")
    for (ls <- allIgnored) yield {
      assert(ls.exists(l => l.contains("target")))
    }
  }


}