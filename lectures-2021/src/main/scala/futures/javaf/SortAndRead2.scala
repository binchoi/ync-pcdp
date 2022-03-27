package futures.javaf

import java.util.concurrent.Executors

import futures.sorting.{ArrayUtil, ScalaSort}

/**
  * Example 1 
  *
  * @author Ilya Sergey
  */
object SortAndRead2 {
  
  def main(args: Array[String]): Unit = {
    val pool = Executors.newCachedThreadPool()
    
    val arr = ArrayUtil.generateRandomArrayOfInts(20000000)
    
    val fut = pool.submit(new Runnable {
      override def run(): Unit = {
        println("Starting to sort...")
        ScalaSort.sort(arr)
      }
    })

    println("Enter something:")
    val line = scala.io.StdIn.readLine()
    println(s"Hmm...'$line', you say? Interesting...")

    println(s"The length of your input is ${line.length}")
    println("Are we done sorting, by the way?")
    fut.get()
    println("Now we're done sorting!")
    pool.shutdown()

  }

}
