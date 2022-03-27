package homework.problem3

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class Problem3Tests extends FunSpec with Matchers {
  
  describe("An array-to-list conversion") {
    it ("should preserve the length of the collection") {
      // TODO: implement me
      val testArray1 = Array(1,2,3,4,5,6)
      val testList1 = Problem3.arrayToList(testArray1)
      assert(testArray1.length == testList1.length)
    }

    it ("should preserve the contents of the collection") {
      // TODO: implement me
      val testArray2 = Array("a", "b", "c", "d", "!")
      val testList2 = Problem3.arrayToList(testArray2)
      val testArray2a = testList2.toArray
      assert(testArray2 sameElements testArray2a)
    }

    it ("should preserve the order of elements of the collection") {
      // TODO: implement me
      val testArray3 = Array(true, false, true, false, true)
      val testList3 = Problem3.arrayToList(testArray3)

      var res = true
      for (i <- testArray3.indices) {
        res = res & (testArray3(i) == testList3(i))
      }
      assert(res)
    }
  }

  describe("A list-to-array conversion") {
    it ("should preserve the length of the collection") {
      // TODO: implement me
      var list1 = List("Hello", "This", "is", "array!")
      val array1 = Problem3.listToArray(list1)
      assert(array1.length == list1.length)
    }

    it ("should preserve the contents of the collection") {
      // TODO: implement me
      var list1 = List("Hello", "This", "is", "array!")
      val array1 = Problem3.listToArray(list1)
      assert(array1 sameElements Array("Hello", "This", "is", "array!"))
    }

    it ("should preserve the order of elements of the collection") {
      // TODO: implement me
      val testList3 = List(true, false, true, false, true)
      val testArray3 = Problem3.listToArray(testList3)

      var res = true
      for (i <- testList3.indices) {
        res = res & (testList3(i) == testArray3(i))
      }
      assert(res)
    }
  }


}
