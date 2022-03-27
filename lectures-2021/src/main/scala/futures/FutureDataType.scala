package futures

import scala.concurrent.Future
import scala.io.Source

/**
 * Example 5
 *
 * @author Ilya Sergey
 */
object FutureDataType {

  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val buildFileFuture: Future[String] =
    // Create the future and starts executing it
      Future {
        val f = Source.fromFile("build.sbt")
        try {
          //          Thread.sleep(500)
          f.getLines.mkString("\n")
        } finally {
          f.close
        }
      }

    println("Started reading build file asynchronously")

    // Probably not completed yet
    println(s"Future completion status: ${buildFileFuture.isCompleted}")
    println(s"Future value: ${buildFileFuture.value}")
    println()

    Thread.sleep(250)
    println("Waited 250 ms...")

    // Future is here!
    println(s"Future completion status: ${buildFileFuture.isCompleted}")
    println()
    // Syncrhonizing with the future: getting the result of the future out of it
    println(s"Result: ${buildFileFuture.value.get}")
  }

}
