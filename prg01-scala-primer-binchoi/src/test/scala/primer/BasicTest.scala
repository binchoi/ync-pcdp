package primer


/**
  * @author Ilya Sergey
  */

import org.scalatest.{FunSpec, Matchers}

// The test needs to extend those two traits in order to
// get access to the functions used below
class BasicTest extends FunSpec with Matchers {

  // Describe a set of tests for some class of cases
  describe("A simple test") {

    // Describe an individual test
    it("should always succeed") {
      // Write your code here
      // Use `assert` statements to make the test pass or fail
      assert(2 * 2 == 4)
    }
  }

  // Another set of tests
  describe("A square function") {
    it ("should work correctly") {
      // Import all from object SquareOf5
      import primer.runners.squares.SquareOf5._
      assert(square(10) == 100)
    }
  }

}
