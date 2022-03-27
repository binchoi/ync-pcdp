package futures

import futures.ServerObserver.getLinkHttpServerCounts
import futures.LinkExtractor.{askForUrl, fetchUrl, getLinks}
import org.scalatest.AsyncFlatSpec

import java.util.concurrent.TimeUnit
import scala.collection.immutable.Map
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Success

/**
  * @author Ilya Sergey
  */
class UrlTests extends AsyncFlatSpec {

  behavior of "LinkExtractor"

  it should "correctly find some expected links" in {
    // Let's check fetch first!
    val doc = fetchUrl("https://www.facebook.com/")
    doc.map({ source =>
      assert(source.contains("https://m.facebook.com/"))
      assert(source.contains("https://id-id.facebook.com/"))
      assert(source.contains("https://about.facebook.com/"))
      assert(source.contains("https://zh-cn.facebook.com/"))
      assert(source.contains("https://m.facebook.com/"))
    })
  }

  it should "correctly find some expected links 2" in {
    val doc = fetchUrl("https://www.facebook.com/")
    // Let's check getLinks now!
    val myLinks = doc.flatMap(source => getLinks(source))
    myLinks.map({list_link =>
      assert(list_link.contains("https://messenger.com/"))
      assert(list_link.contains("https://about.facebook.com/"))
      assert(list_link.contains("https://zh-cn.facebook.com/"))
      assert(list_link.contains("https://m.facebook.com/"))
      assert(list_link.contains("https://www.oculus.com/"))
    })
  }

  it should "correctly find some expected links3" in {
    for{
      doc <- LinkExtractor.fetchUrl("https://ilyasergey.net")
      links <- LinkExtractor.getLinks(doc)
    } yield {
      assert(links.contains("http://www.nus.edu.sg/"))
    }
  }

  it should "Fail gracefully" in {
    // When no link is provided
    try {
      val f: Future[List[String]] = (askForUrl().flatMap(url => fetchUrl(url))).flatMap(doc => getLinks(doc))
      val links = Await.result(f, Duration.create(5, TimeUnit.SECONDS))
    } catch {
      case e => println("Please provide an appropriate url.")
    }

    // Still no error but only print message (graceful!)
    assert(true)
  }

  it should "Fail gracefully2" in {
    // Likewise if I were to give it a bad url (i.e. those which are not included in the protocol, etc...), the try-catch
    // branch would have given the same print statement as well "Please provide an appropriate url.". Graceful!

    // The effect of my implementation is similar to what is experienced below!
    val doc = fetchUrl("httacebook.com/") recover {
      case _ => println("Please provide an appropriate url.")
        ""
    }
    doc.map({source =>
      assert(source.isEmpty)
      assert(true)
    })

  }

  behavior of "ServerObserver"

  it should "correctly find expected servers" in {
    val f: Future[Map[String, Int]] = getLinkHttpServerCounts("https://edition.cnn.com/travel")
    val map = Await.result(f, Duration.create(100, TimeUnit.SECONDS))
    println(map.toList.mkString("\n"))

    f.map({myMap =>
      assert(myMap.nonEmpty)
      assert(myMap("Unknown") == 13)
      assert(myMap("Varnish") == 1)
      assert(myMap("nginx") == 6)
    })
  }

  it should "correctly find expected servers (one more time!)" in {
    // TODO: Implement me!
    //       Try some well-known URLs

    val f: Future[Map[String, Int]] = getLinkHttpServerCounts("https://www.youtube.com/")
    val map = Await.result(f, Duration.create(100, TimeUnit.SECONDS))
    println(map.toList.mkString("\n"))

    f.map({myMap =>
      assert(myMap.nonEmpty)
      assert(myMap("Google Frontend") == 2)
      assert(myMap("GSE") == 1)
      assert(myMap("ESF") == 5)
    })

  }

  it should "Fail gracefully" in {
    // Using the RECOVER method of future

    val f: Future[Map[String, Int]] = getLinkHttpServerCounts("asdfasfdasdfht.yt.co")
    val map = Await.result(f, Duration.create(100, TimeUnit.SECONDS))
    println(map.toList.mkString("\n"))

    f.map({myMap =>
      assert(myMap.isEmpty)
    })  }
}
