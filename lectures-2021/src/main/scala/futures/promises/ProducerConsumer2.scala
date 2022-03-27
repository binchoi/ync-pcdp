package futures.promises

import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}


/**
  * Example 14
  * 
  * @author Ilya Sergey
  */
object ProducerConsumer2 {

  def main(args: Array[String]): Unit = {

    val p = Promise[String]
    val f = p.future

    val producer = Future {
      println(s"[Producer] promise completed: ${p.isCompleted}")
      println("[Producer] Enter something")
      val r = scala.io.StdIn.readLine()
      p success r
      println(s"[Producer] promise completed: ${p.isCompleted}")
      println("[Producer] Input completed")
    }

    
    // The same as before but with extra synchronisation (for the demo purposes)
    val consumer = Future {
      println("[Consumer] Waiting for the input\n")
    }.flatMap { _ =>
      // Going to loop while `p` is not completed
      while (!p.isCompleted) {}
      println(s"[Consumer] promise completed: ${p.isCompleted}")
      f
    }

    val res = Await.result(consumer, Duration.create(10, TimeUnit.SECONDS))
    println(s"Final result: $res")

  }


}
