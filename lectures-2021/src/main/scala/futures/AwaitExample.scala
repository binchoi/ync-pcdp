package futures

import scala.concurrent._
import scala.concurrent.duration.Duration

/**
  * Example 8: Awaiting for future results
  * 
  * @author Ilya Sergey
  */
object AwaitExample {

  implicit val ec = ExecutionContext.global

  def readLineFromInput(): Future[String] = Future {
    println("Input stuff:")
    val f = scala.io.StdIn.readLine()
    // println("Sleeping for 3 sec")
    // Thread.sleep(3000)
    f.toUpperCase
  }

  val inputFuture: Future[String] = readLineFromInput()

  def main(args: Array[String]): Unit = {
    
    val f = inputFuture
    
    import scala.concurrent._

    // This will wait for 5 seconds, and then fail
    val r = Await.result(f, Duration.create(3, duration.SECONDS))
    println(r)
  }

}
