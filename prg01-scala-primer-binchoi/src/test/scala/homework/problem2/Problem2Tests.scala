package homework.problem2

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class Problem2Tests extends FunSpec with Matchers {

  import homework.problem2.Problem2._

  describe("Of the implemented collection combinators") {

    val l = List(1, 2, 3, 4, 5, 6, 7)

    it("`myMap` should behave like its library counterpart") {
      val fun = (x: Int) => x * x
      assert(l.map(fun) == myMap(l, fun))
    }

    it("`myFilter` should behave like its library counterpart") {
      val pred = (x: Int) => x % 2 == 0
      assert(l.filter(pred) == myFilter(l, pred))

    }

    it("`myFlatten` should behave like its library counterpart") {
      val ls = List(l, l.reverse)
      assert(ls.flatten == myFlatten(ls))
    }

    it("`myFoldLeft` should behave like its library counterpart") {
      val folder = (acc: List[Int]) => (e: Int) => e :: acc

      val libResult = l.foldLeft(Nil: List[Int])((acc, e) => folder(acc)(e))
      val myResult = myFoldLeft(l, Nil, folder)

      assert(libResult == myResult)

    }

    it("`myFoldRight` should behave like its library counterpart") {
      val folder = (e: Int) => (acc: List[Int]) => e :: acc

      val libResult = l.foldRight(Nil: List[Int])((acc, e) => folder(acc)(e))
      val myResult = myFoldRight(l, Nil, folder)
      
      assert(libResult == myResult)

    }


  }

}
