package parallel

import org.scalatest.{FunSpec, Matchers}
import parallel.parstring.ParString

import scala.util.Random

/**
  * Example 18: ParString Combiner tests
  */
class ParStringCombinerTests extends FunSpec with Matchers {

  val text = Random.shuffle(("A custom text " * 25000).toVector).mkString("")

  // What are the divide an conquer operations we have?
  val partxt = new ParString(text)

  describe(s"Parallel string implementation with a custom combiner") {
    it("should correctly map") {
      val seqResult = text.map(Character.toUpperCase)
      val parResult = partxt.map(Character.toUpperCase).mkString("")
      assert(parResult == seqResult)
    }


    it("should correctly filter") {
      val seqResult = text.filter(Character.isLowerCase)
      val parResult = partxt.filter(Character.isLowerCase).mkString("")
      assert(parResult == seqResult)
    }
  }

}
