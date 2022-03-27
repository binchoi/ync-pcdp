package futures.sorting

import scala.reflect.ClassTag

/**
  * Utility functions for testing implementations with arrays
  */
object ArrayUtil {


  /**
    * Swaps elements with indices `i` and `j` in a n array `a`.
    *
    * Ensures that both `i` and `j` are in `a`'s range and throws an
    * exception otherwise.
    */
  def swap[T](a: Array[T], i: Int, j: Int): Unit = {
    assert(0 <= i && i < a.length)
    assert(0 <= j && j < a.length)
    val tmp = a(i)
    a(i) = a(j)
    a(j) = tmp
  }

  /**
    * The name says it all. Returns an array of a given size
    * populated with random integers. 
    */
  def generateRandomArrayOfInts(size: Int): Array[Int] = {
    val a = new Array[Int](size)
    val low = 0
    val high = a.size - 1
    for (i <- a.indices) {
      a(i) = generateRandomIntBetween(0, high)
    }
    a
  }

  /**
    * Generates a random integer. Both ends are inclusive. 
    */
  def generateRandomIntBetween(low: Int, high: Int): Int = {
    assert(low <= high, "Low end should be less then high end.")

    val r = math.random()
    val range = high - low
    (range * r).toInt + low
  }

  // This is necessary to enable implicit `Ordering` on `Int`s
  // Do not remove this import!
  import Ordering.Implicits._

  /**
    * Check if an array `a` is sorted 
    */
  def checkSorted[T: Ordering](a: Array[T]): Boolean = {
    if (a.length <= 1) return true
    for (i <- 1 until a.length) {
      if (a(i - 1) > a(i)) {
        return false
      }
    }
    true
  }

  /**
    * Check if an array `a` has the same elements as an array `b`
    */
  def checkSameElements[T: Ordering](a: Array[T], b: Array[T]): Boolean = {
    a.toList.sorted == b.toList.sorted

  }

  /**
    * Copies an array `a` into a new one and returns it
    */
  def arrayCopy[T: ClassTag](a: Array[T]): Array[T] = {
    val aCopy = new Array[T](a.length)
    a.copyToArray(aCopy)
    aCopy
  }

  def partition[T: Ordering](arr: Array[T], lo: Int, hi: Int): Int = {
    if (hi <= lo) return lo
    val pivot = arr(hi - 1)
    var i = lo
    for (j <- lo to hi - 2) {
      if (arr(j) <= pivot) {
        swap(arr, i, j)
        i = i + 1
      }
    }
    swap(arr, i, hi - 1)
    i
  }


}
