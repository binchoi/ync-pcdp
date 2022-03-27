package futures

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.io.Source

/**
 * Example 10: Composing futures
 *
 * @author Ilya Sergey
 */
object ComposingFutures {

  import ExecutionContext.Implicits.global

  val netiquetteUrl = "https://www.ietf.org/rfc/rfc1855.txt"
  val urlSpecUrl = "https://www.w3.org/Addressing/URL/url-spec.txt"

  def findLines(lines: List[String], keyword: String): List[String] = {
    val resuls = lines.zipWithIndex.collect {
      case (line, n) if line.contains(keyword) => (n, line)
    }
    resuls.map(_._2)
  }

  def main(args: Array[String]): Unit = {


    val t1 = System.currentTimeMillis()

    // Future 1: Get all contents from Netiquette URL
    val netiquette = Future {
      val source = Source.fromURL(netiquetteUrl)
      try source.getLines.toList finally source.close()
    }

    // Future 1: Get all contents from URL spec
    val urlSpec = Future {
      val source = Source.fromURL(urlSpecUrl)
      try source.getLines.toList finally source.close()
    }

    // Compose the results of two futures (run sequentially) in a single callback
    val answer = netiquette.flatMap { n =>
      urlSpec.map { u =>
        val ls1 = findLines(n, "good")
        val ls2 = findLines(u, "telnet")
        val res1 = "From Netiquette:" :: ls1
        val res2 = "From URL Spec:" :: ls2
        res1 ++ res2
      }
    }

    // TODO: Alternative way to do it

    //    val answer = for {
    //      n <- netiquette
    //      u <- urlSpec
    //      ls1 = findLines(n, "good")
    //      ls2 = findLines(u, "telnet")
    //    } yield {
    //      ("From Netiquette:" :: ls1) ++ ("From URL Spec:" :: ls2)
    //    }

    // TODO: [Question] How this would be different from the one above?

    //        val answer = for {
    //          n <- Future { Source.fromURL(netiquetteUrl).getLines.toList }
    //          u <- Future { Source.fromURL(urlSpecUrl).getLines.toList }
    //          ls1 = findLines(n, "good")
    //          ls2 = findLines(u, "telnet")
    //        } yield {
    //          ("From Netiquette:" :: ls1) ++ ("From URL Spec:" :: ls2)
    //        }


    val res = Await.result(answer, Duration.create(10, duration.SECONDS))
    val t2 = System.currentTimeMillis()

    println(res.mkString("\n"))

    println(s"Time: ${t2 - t1} ms")

  }

}
