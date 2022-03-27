package futures

import scala.concurrent._
import scala.io.Source

/**
 * Example 6
 *
 * @author Ilya Sergey
 */
object FuturesCallbacks {

  implicit val ec = ExecutionContext.global

  // Get contents from the URL as a future
  def getUrlSpec(): Future[List[String]] = Future {
    val url = "https://www.w3.org/Addressing/URL/url-spec.txt"
    val f = Source.fromURL(url)
    try {
      f.getLines.toList
    } finally {
      f.close()
    }
  }

  // A pure function that looks for lines with a keyword
  def find(lines: List[String], keyword: String): String = {
    val resuls = lines.zipWithIndex.collect {
      case (line, n) if line.contains(keyword) => (n, line)
    }
    resuls.mkString("\n")
  }

  def main(args: Array[String]): Unit = {

    val urlSpec: Future[List[String]] = getUrlSpec()

    // TODO: Callback 1: Printing lines

    //    urlSpec.foreach({ lines =>
    //      val ls = lines.mkString("\n")
    //      println(ls)
    //    })
    // TODO: The same way to write it
    //    for (lines <- urlSpec) {
    //      println(lines.mkString("\n"))
    //    }

    // TODO: Looking for keyword and printing all lines in a callback


    //        urlSpec
    //          .map(lines => find(lines, "telnet"))
    //          .foreach(s => println(s))

    // TODO: The same one, but more fancy

    (for (lines <- urlSpec) yield {
      find(lines, "telnet")
    }).foreach(s => println(s))


    Thread.sleep(3000)

  }

}
