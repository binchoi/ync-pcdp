package parallel

import org.scalatest.{FunSpec, Matchers}
import parallel.parstring.ParString

import scala.util.Random

/**
 * Example 15: ParString Splitter tests
 */
class ParStringSplitterTests extends FunSpec with Matchers {

  val text = Random.shuffle(("A custom text " * 250000).toVector).toString

  // What are the divide an conquer operations we have?
  val partxt = new ParString(text)

  describe(s"Parallel string implementation") {
    it("should correctly count") {
      val seqResult = text.count(Character.isUpperCase)
      val parResult = partxt.count(Character.isUpperCase)
      assert(parResult == seqResult)
    }


    it("should correctly implement custom based on splitting") {
      val seqResult = text.foldLeft(0)((n, c) => if (Character.isUpperCase(c)) n + 1 else n)
      val parResult = partxt.aggregate(0)((n, c) => if (Character.isUpperCase(c)) n + 1 else n, _ + _)
      assert(parResult == seqResult)
    }
  }

}
