package homework.problem1

import org.scalatest.{FunSpec, Matchers}

/**
  *
  * Functional programming in Scala 
  *
  * @author Ilya Sergey
  */
class Problem1Tests extends FunSpec with Matchers {

  // Yes, you can import all definitions from an object 
  // as it were a package
  import homework.problem1.Problem1._

  describe("The function `compose`") {

    it("can be used to obtain the `plus two` function") {
      val plusOne = (x: Int) => x + 1
      val plusTwo = compose(plusOne, plusOne)

      // Random integer number between 0 and 1000
      val randomNumber = (math.random() * 1000).toInt

      assert(plusTwo(randomNumber) == randomNumber + 2)
    }

    it("should correctly compose some other two arithmetic functions") {
      val timesFive = (x: Int) => 5*x
      val minusThree = (x: Int) => x-3
      val minusThreeTimesFive = compose(timesFive, minusThree)
      // Random integer number between 0 and 1000
      val randomNumber2 = (math.random() * 1000).toInt

      assert(minusThreeTimesFive(randomNumber2) == 5*(randomNumber2-3))
    }

    it("should compose functions of different types") {
      val timesTen = (x: Int) => 10*x
      val convertString = (x: Int) => x.toString + " is the answer!"
      val computeAndStringify = compose(convertString, timesTen)

      val randomNumber3 = (math.random() * 1000).toInt
      assert(computeAndStringify(randomNumber3) == (randomNumber3*10).toString + " is the answer!")
    }

  }

  describe("The function `fuse`") {
    it("should correctly fuse a pair of Some-s") {
      val p = Some(42)
      val q = Some("aaa")
      fuse(p, q) match {
        case Some(value) =>
          assert(value._1 == 42)
          assert(value._2 == "aaa")
        case None =>
          // This should not happen
          assert(false)
      }
    }

    it("should do something sensible if one of the components is None") {
      val p = None
      val q = Some("aaa")
      fuse(p, q) match {
        case Some(value) =>
          // this should not happen
          assert(false)
        case None =>
          assert(true)
      }
    }
  }

  describe("The function `check`") {
    it("should work correctly on a simple list") {
      assert(check(1 until 10, (x: Int) => 40 / x > 0))
    }

    it("should work correctly on your example") {
      assert(check(10 until 100 by 10, (x: Int) => 10*x >= 100))
    }
  }

  describe("the `Pair` class") {
    it("should be possible to use for pattern matching") {
      val nativePair = (42, "abc")

      // TODO: create your pair with the same components and pattern match on it, and on `nativePair`
      // comparing them component-wise
      val pair1 = new Pair(42, "abc")
      // several ways to pattern match

      (pair1, nativePair) match {
        case (a,b) => assert(a.fst == b._1 & a.snd == b._2)
        case value => assert(false)
      }

      // Other ways to pattern match
      pair1 match {
        case value => assert(value.fst==nativePair._1, value.snd==nativePair._2)
        case value => assert(false)
      }
      nativePair match {
        case (fst, snd) => assert(fst ==42 & snd=="abc")
        case value => assert(false)
      }



    }

  }


}
