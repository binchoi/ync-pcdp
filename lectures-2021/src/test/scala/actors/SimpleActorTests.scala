package actors

/**
  * Example 07: testing actor systems
  */

import akka.actor.Props
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SimpleActorTests extends TestKit(ourSystem)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  "A counter actor" must {

    "tell when the counting is done" in {
      val counter = system.actorOf(Props[CountDownActorWithResponse])

      for (i <- 0 to 9) {
        counter ! "count"
      }
      // At this point block and expect a message 
      expectMsg("Done counting")

      counter ! "howdy?"

      expectMsg("Done counting")

      // TODO: What happens if we ask for something else?
    }

    "tell when the counting is done to many threads" in {
      val counter = system.actorOf(Props[CountDownActorWithResponse])

      for (i <- 0 to 9) {
        counter ! "count"
      }

      class MyThread extends Thread {
        override def run() = {
          // Concurrently send messages to that actor
          counter ! "howdy?"
          expectMsg("Done counting")
        }
      }

      val t1 = new MyThread()
      val t2 = new MyThread()

      t1.start()
      t2.start()
      t1.join()
      t2.join()
    }
    
  }


  // Make sure to shutdown all actors in the system
  override def afterAll: Unit = {
    // If uncommented, might conflict with other tests
    // TestKit.shutdownActorSystem(system)
  }

}

