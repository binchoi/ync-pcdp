package actors

import actors.DictionaryActor._
import akka.actor.Props
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
  * Testing DictionaryActor
  */
class DictionaryActorTests extends TestKit(ourSystem)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  // Initialise the actor once for all tests
  val dict = system.actorOf(Props[DictionaryActor])
  
  // Send it the path to initial dictionary
  dict ! Init("./src/main/resources/words.txt")
  
  var done = false

  "A dictionary actor" must {
    
    "respond positively about existing words" in {
      dict ! IsWord("abracadabra")
      dict! "hohoho"
      expectMsg(true)
    }
    
    "respond negatively about non-existing words" in {
      dict ! IsWord("asdfgasdgads")
      expectMsg(false)
    }

    "respond appropriately when uninitialized" in {
      val d1 = system.actorOf(Props[DictionaryActor])
      d1 ! IsWord("cat")
      expectMsg("uninitialized")

      d1 ! Init("./src/main/resources/words.txt")
      Thread.sleep(100)
      d1 ! IsWord("cat")
      expectMsg(true)
      
      d1 ! End
      d1 ! IsWord("cat")
      expectMsg("uninitialized")
    }
    
  }

  // Make sure to shutdown all actors in the system
  override def afterAll: Unit = {
    // TODO: terminate the dictionary
    dict ! End
    // TestKit.shutdownActorSystem(system)
  }


}
