package parallel.caveats

import java.util.concurrent.ConcurrentSkipListSet

import parallel.ParHtmlSearch._

import scala.collection._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
Example 12: Using with concurrent collections

Using parallel and concurrent collections in tandem.

 */
object ConcurrentCollectionsWrong {

  def intersection(a: GenSet[String], b: GenSet[String]): GenSet[String] = {
    val result = new mutable.HashSet[String]
    // Anything wrong here?
    for (x <- a.par) if (b contains x) result.add(x)
    result
  }

  def main(args: Array[String]): Unit = {

    val ifut = for {
      htmlSpec <- getHtmlSpec()
      urlSpec <- getUrlSpec()
    } yield {
      val htmlWords = htmlSpec.mkString.split("\\s+").toSet // Ordinary set
      val urlWords = urlSpec.mkString.split("\\s+").toSet // Ordinary set
      intersection(htmlWords, urlWords)
    }

    ifut onComplete {
      case Success(t) =>
        println(s"Result size: ${t.size}")
        println(s"Result: $t")
      case Failure(r) =>
        println(s"Failure: $r")
    }

    Thread.sleep(10000)
  }
}



/*
This is a better implementation that uses ConcurrentSkipList from Java concurrent library 
 */
object ConcurrentCollectionsGood extends App {

  import JavaConverters._
  
  def intersection(a: GenSet[String], b: GenSet[String]): GenSet[String] = {
    val skiplist = new ConcurrentSkipListSet[String]
    for (x <- a.par) if (b contains x) skiplist.add(x)
    skiplist.asScala
  }

  val ifut = for {
    htmlSpec <- getHtmlSpec()
    urlSpec <- getUrlSpec()
  } yield {
    val htmlWords = htmlSpec.mkString.split("\\s+").toSet // Ordinary set
    val urlWords = urlSpec.mkString.split("\\s+").toSet // Ordinary set
    intersection(htmlWords, urlWords)
  }

  ifut onComplete {
    case Success(t) =>
      println(s"Result size: ${t.size}")
      println(s"Result: $t")
    case Failure(r) =>
      println(s"Failure: $r")
  }

  Thread.sleep(10000)

}