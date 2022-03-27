package basic

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class BasicTest extends FunSpec with Matchers {
  
  describe("A simple test") {
    it ("should always succeed") {
      assert(2 * 2 == 4)
    }

  }

}
