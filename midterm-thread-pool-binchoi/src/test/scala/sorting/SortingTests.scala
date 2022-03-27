package sorting

import org.scalatest.{FunSpec, Matchers}

/**
  * A generic trait for testing sorting implementations
  */
trait SortingTests extends FunSpec with Matchers {

  val sorter: Sorting
  
  val ARRAY_SIZE: Int = 5000
  val BIGGER_ARRAY_SIZE: Int = 10000000
  val ARRAY_SIZE_ONE: Int = 1

  describe(s"A Sorting implementation from ${this.getClass.getSimpleName}") {
    it("should sort an array correctly") {

      // Generate random array
      val a = ArrayUtil.generateRandomArrayOfInts(ARRAY_SIZE)
      // Copy the array to keep the original result
      val aCopy = ArrayUtil.arrayCopy(a)

      val t1: Long = System.currentTimeMillis()
      sorter.sort(a)
      val t2: Long = System.currentTimeMillis()
      val formatter = java.text.NumberFormat.getIntegerInstance


      // TODO: Check that the resulting array is a sorted version of the original random one
      // TODO: Use functions implemented in `ArrayUtil`
      assert(ArrayUtil.checkSameElements(a, aCopy))
      assert(ArrayUtil.checkSorted(a))
    }
//
//    it ("should correctly sort large arrays with great variance") {
//      // Generate array with little variance and lots of equal elements
//      val a = ArrayUtil.generateRandomArrayOfInts(BIGGER_ARRAY_SIZE)
//      val aCopy = ArrayUtil.arrayCopy(a)
//
//      val t1: Long = System.currentTimeMillis()
//      sorter.sort(a)
//      val t2: Long = System.currentTimeMillis()
//      val formatter = java.text.NumberFormat.getIntegerInstance
//      println()
//      println(s"[Array size $BIGGER_ARRAY_SIZE] ${sorter.getName} time: ${formatter.format(t2 - t1)} ms")
//
//      assert(ArrayUtil.checkSameElements(a, aCopy))
//      assert(ArrayUtil.checkSorted(a))
//      //      for (i <- a) {println(i)}
//    }
//
//    it ("should correctly sort large arrays with little variance") {
//      // Generate array with little variance and lots of equal elements
//      val a = Array.fill(BIGGER_ARRAY_SIZE){ArrayUtil.generateRandomIntBetween(0, 10)} ////////////////////////////////////////change back to big
//      val aCopy = ArrayUtil.arrayCopy(a)
//
//      val t1: Long = System.currentTimeMillis()
//      sorter.sort(a)
//      val t2: Long = System.currentTimeMillis()
//      val formatter = java.text.NumberFormat.getIntegerInstance
//      println()
//      println(s"[Array size $BIGGER_ARRAY_SIZE] ${sorter.getName} time: ${formatter.format(t2 - t1)} ms")
//
//      assert(ArrayUtil.checkSameElements(a, aCopy))
//      assert(ArrayUtil.checkSorted(a))
//    }
//
//    it ("should correctly sort small arrays with only one element") {
//      // Generate array with little variance and lots of equal elements
//      val a = Array.fill(ARRAY_SIZE_ONE){ArrayUtil.generateRandomIntBetween(0, 10)}
//      val aCopy = ArrayUtil.arrayCopy(a)
//
//      val t1: Long = System.currentTimeMillis()
//      sorter.sort(a)
//      val t2: Long = System.currentTimeMillis()
//      val formatter = java.text.NumberFormat.getIntegerInstance
//      println()
//      println(s"[Array size $ARRAY_SIZE_ONE] ${sorter.getName} time: ${formatter.format(t2 - t1)} ms")
//
//      assert(ArrayUtil.checkSameElements(a, aCopy))
//      assert(ArrayUtil.checkSorted(a))
//    }
//
//    it ("should correctly sort arrays with no variance") {
//      // Generate array with little variance and lots of equal elements
//      val a = Array.fill(BIGGER_ARRAY_SIZE){1}
//      val aCopy = ArrayUtil.arrayCopy(a)
//
//      val t1: Long = System.currentTimeMillis()
//      sorter.sort(a)
//      val t2: Long = System.currentTimeMillis()
//      val formatter = java.text.NumberFormat.getIntegerInstance
//      println()
//      println(s"[Array size $BIGGER_ARRAY_SIZE] ${sorter.getName} time: ${formatter.format(t2 - t1)} ms")
//
//      assert(ArrayUtil.checkSameElements(a, aCopy))
//      assert(ArrayUtil.checkSorted(a))
//    }
  }

}
