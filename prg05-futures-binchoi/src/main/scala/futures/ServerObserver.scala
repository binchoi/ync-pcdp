package futures

import java.net.URL
import java.util.concurrent.TimeUnit
import scala.collection.immutable.Map
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.collection.mutable.ListBuffer

 
/** Problem 2

Implement a method `getLinkHttpServerCounts(url: String): Future[Map[String, Int]]`,
which, given a URL, reads the web page at that URL, finds all the hyperlinks, visits each of them concurrently, 
and locates the Server HTTP header for each of them. It then collects a map of which servers were
found how often. Use futures for visit each page and return its Server header.   
  
Hints:

* You may reuse some of the functionality from Problem 1

* Feel free to use the utility functions `fetchServerName` and `toUrl`. 
  
* Use `Future.sequence` utility method to combine a sequence of Futures
  into a future returning sequence.     
  
  */
object  ServerObserver {

  private implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  import LinkExtractor._
  
  def getLinkHttpServerCounts(url: String): Future[Map[String, Int]] =  {


    // TODO: Implement me!
    val f: Future[List[String]] = fetchUrl(url).flatMap(doc => getLinks(doc))
    val lstFut = {
      for {lst_of_str <- f} yield {
        lst_of_str.map(url_as_str => Future{toUrl(url_as_str) match {
          case Success(url) => fetchServerName(url)
          case Failure(e) => Some("ERROR")
        }})
      }
    }

    val futSeq = {
      lstFut.flatMap(lst_fut => Future.sequence(lst_fut))
    }

    val res = futSeq.map(resOut => resOut.groupBy(i=>i).mapValues(_.size))

    val myMap = res.map({ myMap =>
      myMap.map({
        case tup@(Some(_), _) => (tup._1.get, tup._2)
        case tup => ("Unknown", tup._2) // if source is unknown, mark as unknown!
      })
    })
    myMap

  } recover {
    case _ => println("There has been an error. Reconsider your url")
      Map()
  }

  private def fetchServerName(url: URL): Option[String] = {
    println(s"Fetching header for $url")
    val name = Option(url.openConnection().getHeaderField("Server"))
    if (name.isDefined) println(s"$url uses server: ${name.get}")
    else println(s"$url does not expose server name")
    name
  }

  private def toUrl(link: String): Try[URL] = {
    Try(new URL(link.stripPrefix("href=\"").stripSuffix("\""))) // such that we can use output of link extractor
  }


  def main(args: Array[String]): Unit = {
    // TODO: Implement me (concisely)!
    val f: Future[Map[String, Int]] = getLinkHttpServerCounts("https://edition.cnn.com/travel")

    val map = Await.result(f, Duration.create(100, TimeUnit.SECONDS))
    println(map.toList.mkString("\n"))
  }
}


