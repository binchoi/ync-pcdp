package futures.javaf

import futures.sorting.{ArrayUtil, ScalaSort}

/**
  * Example 0
  * @author Ilya Sergey
  */
object SortAndRead {

  def main(args: Array[String]): Unit = {
    val arr = ArrayUtil.generateRandomArrayOfInts(20000000)

    println("Starting to sort...")
    ScalaSort.sort(arr)
    println("Are we done sorting, by the way?")
    println("Now we're done sorting!")

    println("Enter something:")
    val line = scala.io.StdIn.readLine()
    println(s"Hmm...'$line', you say? Interesting...")

    println(s"The length of your input is ${line.length}")
  }

}
