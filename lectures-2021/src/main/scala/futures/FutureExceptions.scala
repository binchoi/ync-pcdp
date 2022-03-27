package futures

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Example 7
  *  
  * @author Ilya Sergey
  */
object FutureExceptions {

  implicit val ec = ExecutionContext.global

  def getUrlSpec(url: String): Future[List[String]] = Future {
    val f = Source.fromURL(url)
    try {
      f.getLines.toList
    } finally {
      f.close()
    }
  }

  def main(args: Array[String]): Unit = {
    
    val goodUrl = "https://www.w3.org/Addressing/URL/url-spec.txt"
    val badUrl = "httpz://www.w3.org/Addressing/URL/url-spec.txz"
    
    val callback: Try[List[String]] => Unit = {
      case Success(value) =>
        println(s"Success: ${value.toString}")
      case Failure(exception) =>
        println(s"Failure: ${exception.toString}")
    }
    
    // TODO: This should succeed and print some text
    // getUrlSpec(goodUrl).onComplete(callback)

    // TODO: This should fail really fast.
    getUrlSpec(badUrl).onComplete(callback)
    
    Thread.sleep(3000)
  }

}
