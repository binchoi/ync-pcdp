package futures

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.Source
import scala.util.matching.Regex

/** Problem 1


Using Scala's Futures and Promises, implement three functions:

* `askForUrl(): Future[String]` asks the user for a URL
* `fetchUrl(url: String): Future[String]` reads the web page at that URL
* `getLinks(doc: String): Future[List[String]]` returns all the hyperlinks from the text of the page 

Each function should use a separate Future for each of these three steps. Test your result in the `main` method

Hints:

* Make sure to handle possible failures via `recover` method

* In case of getting `java.nio.charset.MalformedInputException`, check this post:
  https://stackoverflow.com/questions/29987146/using-result-from-scalas-fromurl-throws-exception

* You can detect links via scala regular expressions:

  val pattern: Regex = """href="(http[^"]+)"""".r // Defines the pattern  
  Next, find out (e.g., via Google) how to find regex patterns in Scala strings

    
*/
object LinkExtractor {

  private implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  def getLinks(doc: String): Future[List[String]] = Future {
    // TODO: Implement me!
    val pattern: Regex = """href="(http[^"]+)"""".r
    (pattern.findAllIn(doc).matchData map {m => m.group(1)}).toList
  }

  def fetchUrl(url: String): Future[String] = Future {
    // TODO: Implement me!
    println(f"Fetching $url")
    val source = Source.fromURL(url)
    try source.mkString finally source.close()
  }

  def askForUrl(): Future[String] = Future {
    println("Enter a valid URL:")
    val url = scala.io.StdIn.readLine()
    url
  }

  def main(args: Array[String]): Unit = {
    try {
      val f: Future[List[String]] = (askForUrl().flatMap(url => fetchUrl(url))).flatMap(doc => getLinks(doc))
      val links = Await.result(f, Duration.create(10, TimeUnit.SECONDS))

      println(links.mkString("\n"))
    } catch {
      case e => println("Please provide an appropriate url.")
    }

  }


  object main
}