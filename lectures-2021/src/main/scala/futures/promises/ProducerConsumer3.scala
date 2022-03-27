package futures.promises

import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}


/**
  * Example 15
  * 
  * @author Ilya Sergey
  */
object ProducerConsumer3 {

  def main(args: Array[String]): Unit = {

    val p = Promise[String]
    val f = p.future

    val producer = Future {
      println("[Producer] Enter something")
      val r = scala.io.StdIn.readLine()
      if (r.startsWith("a")) {
        p.failure(new IllegalArgumentException("Bad input!"))
      } else {
        p.success(r)
      }
      println("[Producer] Input completed")
    }

    // Consumer handling exceptions from `f` in a callback
    val consumer = Future {
      println("[Consumer] Waiting for the input\n")
    }.flatMap { _ =>
      f
    }.recover{
      case e: Exception =>
        // println(p.isCompleted)
        e.toString
    }

    val res = Await.result(consumer, Duration.create(10, TimeUnit.SECONDS))
    println(s"Final result: $res")

  }


}
