package futures.promises

import java.util.concurrent.TimeUnit

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Example 13: Promises
  * 
  * Do not confuse with OCaml LWT and React.js promises!
  * Their "promises" are essentially Scala Futures. :)
  * 
  * @author Ilya Sergey
  */
object ProducerConsumer {

  def main(args: Array[String]): Unit = {

    // Made a promise
    // Aka: single-assignment variable
    val p = Promise[String]
    
    // Handle to the future associated with this promies
    val f = p.future

    // Make a future that will put its result into promise `p`
    // It will also "activate" the corresponding future `f`
    val producer = Future {
      println("[Producer] Enter something")
      val r = scala.io.StdIn.readLine()
      p success r
      println("[Producer] Input completed")
    }

    // Print the message and wait for `f` to complete
    val consumer = Future {
      println("[Consumer] Waiting for the input:\n")
    }.flatMap { _ => f }
    
    val res = Await.result(consumer, Duration.create(10, TimeUnit.SECONDS))
    println(s"Final result: $res")

  }


}
