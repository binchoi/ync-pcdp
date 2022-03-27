package futures.javaf

import java.util.concurrent.{Callable, Executors}

import futures.sorting.{ArrayUtil, ScalaSort}

/**
  * Example 2
  * @author Ilya Sergey
  */
object SortAndRead3 {
  
  def main(args: Array[String]): Unit = {
    val pool = Executors.newCachedThreadPool()
    
    val arr = ArrayUtil.generateRandomArrayOfInts(20000000)
    
    val fut = pool.submit(new Runnable {
      override def run(): Unit = {
        println("Starting to sort...")
        ScalaSort.sort(arr)
      }
    })
    
    val fut2 = pool.submit(new Callable[Int] {
      override def call(): Int = {
        println("Enter something:")
        val line = scala.io.StdIn.readLine()
        println(s"Hmm...'$line', you say? Interesting...")
        line.length()
      }
    })

    println("Are we done sorting, by the way?")
    fut.get()
    println("Now we're done sorting!")

    println(s"The length of your input is ${fut2.get()}")
    
    pool.shutdown()

  }

}
