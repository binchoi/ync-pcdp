package parheap

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
    // assert(0 <= i && i < a.length)
    // assert(0 <= j && j < a.length)
    val tmp = a(i)
    a(i) = a(j)
    a(j) = tmp
  }

  /**
    * The name says it all. Returns an array of a given size
    * populated with random integers. 
    */
  def generateRandomArrayOfInts(size: Int): Array[Int] =
    generateRandomArrayOfBoundedInts(size, size)

  def generateRandomArrayOfBoundedInts(size: Int, limit: Int): Array[Int] =
    Array.fill[Int](size){generateRandomIntBetween(0, limit)}

  def generateRandomString(length: Int) : String = {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890"

    val sb = new StringBuilder

    for(_ <- 0 until length) {
      sb.append(chars(generateRandomIntBetween(0, chars.length - 1)))
    }

    sb.mkString
  }

  def generateRandomStringArray(size: Int, maxlength: Int): Array[String] =
    Array.fill[String](size){generateRandomString(generateRandomIntBetween(1, maxlength))}


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
    // Operators like <, >, <= etc are provided by the `Ordering` constraint

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


}
