package parallel

import scala.collection.Seq
import scala.concurrent.Future
import scala.io.Source

/**
  * Utility methods
  */
object ParHtmlSearch {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getHtmlSpec() = Future {
    val specSrc: Source = Source.fromURL("http://www.w3.org/MarkUp/html-spec/html-spec.txt")
    try specSrc.getLines.toArray finally specSrc.close()
  }

  def getUrlSpec(): Future[Seq[String]] = Future {
    val f = Source.fromURL("http://www.w3.org/Addressing/URL/url-spec.txt")
    try f.getLines.toList finally f.close()
  }

}
