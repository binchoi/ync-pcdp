package sorting

import org.apache.commons.io.filefilter.FalseFileFilter

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
    // Implemented according to the specification above
    val lenArr = a.length
    // if i or j are invalid indices of the array...
    if (!(0 until lenArr).contains(i) || !(0 until lenArr).contains(j)) {
      throw new IndexOutOfBoundsException("swap error: i and/or j is out of index bound")
    }
    // if same index, do nothing
    if (i == j) {
      return
    }
    val tmp = a(i)
    a(i) = a(j)
    a(j) = tmp
  }

  /**
    * The name says it all. Returns an array of a given size
    * populated with random integers. 
    */
  def generateRandomArrayOfInts(size: Int): Array[Int] = {
    val resArray = Array.fill(size){scala.util.Random.nextInt} // is this appropriate? Should I cap at some value
    resArray
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
    // Implemented by relying on the standard comparison operations.
    // Operators like <, >, <= etc are provided by the `Ordering` constraint
    var prev = a(0)
    for (i <- a.slice(1, a.length)) {
      if (i<prev) {
        return false
      }
      prev = i
    }
    true
  }

  /**
    * Check if an array `a` has the same elements as an array `b`
    */
  def checkSameElements[T: Ordering](a: Array[T], b: Array[T]): Boolean = {
    // TODO: Implement in any convenient way
    // TODO: Try to get better-than-O(n^2) implementation
    a.sorted sameElements b.sorted
    // Time Complexity : O(n log n)
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
