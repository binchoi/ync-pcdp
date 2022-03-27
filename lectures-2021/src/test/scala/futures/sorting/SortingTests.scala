package futures.sorting

import org.scalatest.{FunSpec, Matchers}

/**
  * A generic trait for testing sorting implementations
  */
trait SortingTests extends FunSpec with Matchers {

  val sorter: Sorting
  
  val ARRAY_SIZE: Int = 5000

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
      println()
      println(s"[Array size $ARRAY_SIZE] ${sorter.getName} time: ${formatter.format(t2 - t1)} ms")


      // TODO: Check that the resulting array is a sorted version of the original random one
      // TODO: Use functions implemented in `ArrayUtil`
      
      assert(ArrayUtil.checkSorted(a), "The result should be sorted")
      assert(ArrayUtil.checkSameElements(a, aCopy), "The result should have the same elements as original")
      
    }
  }

}
