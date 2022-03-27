package actortree

import actortree.ActorTreeNode.CopyFinished
import akka.actor.{ActorIdentity, ActorRef, ActorSystem, Identify, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random


class ActorTreeTests extends TestKit(ActorSystem("ActorTreeSuite"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  import actortree.ActorTreeSet._

  "An actor-based tree set" must {
    "correctly implement inserts and lookups" in {
      val topNode = system.actorOf(Props[ActorTreeSet])

      topNode ! Contains(testActor, id = 1, 1)
      expectMsg(ContainsResult(1, false))

      topNode ! Insert(testActor, id = 2, 1)
      topNode ! Contains(testActor, id = 3, 1)

      expectMsg(OperationFinished(2))
      expectMsg(ContainsResult(3, true))

    }

    "correctly implement inserts, deletes, lookups, etc..." in {
      val topNode = system.actorOf(Props[ActorTreeSet])

      topNode ! Contains(testActor, id = 1, 1)
      expectMsg(ContainsResult(1, false))

      topNode ! Insert(testActor, id = 2, 1)
      topNode ! Contains(testActor, id = 3, 1)

      expectMsg(OperationFinished(2))
      expectMsg(ContainsResult(3, true))

      for (i <- 4 to 10) topNode ! Insert(testActor, i, i)
      for (i <- 4 to 10) expectMsg(OperationFinished(i))
      for (i <- 6 to 10) topNode ! Remove(testActor, i, i)
      for (i <- 6 to 10) expectMsg(OperationFinished(i))

      topNode ! Contains(testActor, 20, 4)
      topNode ! Contains(testActor, 21, 6)
      topNode ! Contains(testActor, 22, 9)
      expectMsg(ContainsResult(20, true))
      expectMsg(ContainsResult(21, false))
      expectMsg(ContainsResult(22, false))
    }

    "support a reasonable sequence of operations" in {

      // Another way to get a test actor
      val requester = TestProbe()
      
      val requesterRef = requester.ref
      val ops = List(
        Insert(requesterRef, id = 100, 1),
        Contains(requesterRef, id = 50, 2),
        Remove(requesterRef, id = 10, 1),
        Insert(requesterRef, id = 20, 2),
        Contains(requesterRef, id = 80, 1),
        Contains(requesterRef, id = 70, 2)
      )

      val expectedReplies = List(
        OperationFinished(id = 10),
        OperationFinished(id = 20),
        ContainsResult(id = 50, false),
        ContainsResult(id = 70, true),
        ContainsResult(id = 80, false),
        OperationFinished(id = 100)
      )

      verify(requester, ops, expectedReplies)
    }

    "correctly implement inserts, deletes, lookups, etc... (in tandem with GC - transparent)" in {
      val topNode = system.actorOf(Props[ActorTreeSet])

      topNode ! Contains(testActor, id = 1, 1)
      expectMsg(ContainsResult(1, false))

      topNode ! Insert(testActor, id = 2, 1)
      topNode ! Contains(testActor, id = 3, 1)

      expectMsg(OperationFinished(2))
      expectMsg(ContainsResult(3, true))

      for (i <- 4 to 10) topNode ! Insert(testActor, i, i)
      for (i <- 4 to 10) expectMsg(OperationFinished(i))
      for (i <- 6 to 10) topNode ! Remove(testActor, i, i)

      topNode ! GC

      for (i <- 6 to 10) expectMsg(OperationFinished(i))

      topNode ! Contains(testActor, 20, 4)
      topNode ! Contains(testActor, 21, 6)
      topNode ! Contains(testActor, 22, 9)
      expectMsg(ContainsResult(20, true))
      expectMsg(ContainsResult(21, false))
      expectMsg(ContainsResult(22, false))
    }

    "behave identically to built-in set (includes GC)" in {

      val rnd = new Random()

      def randomOperations(requester: ActorRef, count: Int): Seq[Operation] = {
        def randomElement: Int = rnd.nextInt(100)

        def randomOperation(requester: ActorRef, id: Int): Operation = rnd.nextInt(4) match {
          case 0 => Insert(requester, id, randomElement)
          case 1 => Insert(requester, id, randomElement)
          case 2 => Contains(requester, id, randomElement)
          case 3 => Remove(requester, id, randomElement)
        }

        for (seq <- 0 until count) yield randomOperation(requester, seq)
      }

      def referenceReplies(operations: Seq[Operation]): Seq[OperationReply] = {
        var referenceSet = Set.empty[Int]

        def replyFor(op: Operation): OperationReply = op match {
          case Insert(_, seq, elem) =>
            referenceSet = referenceSet + elem
            OperationFinished(seq)
          case Remove(_, seq, elem) =>
            referenceSet = referenceSet - elem
            OperationFinished(seq)
          case Contains(_, seq, elem) =>
            ContainsResult(seq, referenceSet(elem))
        }

        for (op <- operations) yield replyFor(op)
      }

      val requester = TestProbe()
      val topNode = system.actorOf(Props[ActorTreeSet])
      val count = 1000

      val ops = randomOperations(requester.ref, count)
      val expectedReplies = referenceReplies(ops)

      ops foreach { op =>
        topNode ! op
        if (rnd.nextDouble() < 0.1) topNode ! GC
      }
      receiveN(requester, ops, expectedReplies)
    }


  ///////////////////////////////////////////////////////////////////////////////////////////
  // Auxiliary functions
  ///////////////////////////////////////////////////////////////////////////////////////////

  def receiveN(requester: TestProbe, ops: Seq[Operation],
               expectedReplies: Seq[OperationReply]): Unit =
    requester.within(5.seconds) {
      val repliesUnsorted = for (i <- 1 to ops.size) yield try {
        requester.expectMsgType[OperationReply]
      } catch {
        case ex: Throwable if ops.size > 10 => fail(s"failure to receive confirmation $i/${ops.size}", ex)
        case ex: Throwable => fail(s"failure to receive confirmation $i/${ops.size}\nRequests:" + ops.mkString("\n    ", "\n     ", ""), ex)
      }
      val replies = repliesUnsorted.sortBy(_.id)
      if (replies != expectedReplies) {
        val pairs = (replies zip expectedReplies).zipWithIndex filter (x => x._1._1 != x._1._2)
        fail("unexpected replies:" + pairs.map(x => s"at index ${x._2}: got ${x._1._1}, expected ${x._1._2}").mkString("\n    ", "\n    ", ""))
      }
    }

  def verify(probe: TestProbe, ops: Seq[Operation], expected: Seq[OperationReply]): Unit = {
    val topNode = system.actorOf(Props[ActorTreeSet])

    ops foreach { op =>
      topNode ! op
    }

    receiveN(probe, ops, expected)
  }

  // This test takes a little while to run (4-5 seconds)
  "correctly implement a useful GC" in {

    val requester = TestProbe()
    val topNode = system.actorOf(Props[ActorTreeSet], "topNode")

    // First insert 500 elements in random order. We don't insert 0 so it's easier to reason about counting the root.
    for(i <- (1 to 500).toList) {
      topNode ! Insert(requester.ref, i, i)
    }

    // Remove elements 1-250 (so there are 250 left)
    for(i <- 1 to 250) topNode ! Remove(requester.ref, i, i)

    // Initiate garbage collection
    topNode ! GC

    // When we receive a reply that this operation is complete, we know that GC has completed
    // We use a contains operation because it doesn't modify the tree structure
    topNode ! Contains(requester.ref, 1, 1)

    // We're going to get a lot of messages about the insert and remove operations finishing.
    // We don't really care about them. Instead, we wait until we get a reply for our contains
    // request.
    requester.fishForMessage(5.seconds){
      case ContainsResult(1, false) => true // We remove 1, so we should get false back
      case _ => false
    }

    // Here we find the number of physical nodes in the tree, including the root.
    // The wildcard in Akka only matches *immediate* children, so we find all children by levels
    // by counting the number of nodes at one level, and then appending '/*' to the path to go
    // to the next level.
    val sb = new StringBuilder(s"${topNode.path}/*")

    // The total number of nodes we've found
    var childrenAfterGC = 0

    // In a worst case scenario, the height of the tree with a properly-working GC is 250. We check a few additional
    // levels just to be sure that we catch if there are too many physical nodes left.
    for(i <- 1 to 260) {

      val topNodeChildrenPath = sb.mkString

      // system.log.info(s"(Level $i) Found $childrenAfterGC so far. Looking for children at $topNodeChildrenPath...")

      system.actorSelection(topNodeChildrenPath) ! Identify("")

      var receivedValidReply = true

      // We keep trying to receive messages until there aren't any.
      // To make the test terminate in a reasonable amount of time, we have a 15 ms timeout. This seems to work.
      while(receivedValidReply) {
        try{
          expectMsgPF(20.millisecond){
            case ActorIdentity(_, Some(_)) =>
              childrenAfterGC += 1 // We got a reply from an actual node
            case _ =>
              // system.log.info(s"(Level $i) Found $childrenAfterGC so far. No children at this level.")
              receivedValidReply = false // There are no nodes at this level, so we can stop
          }
        } catch{
          case ex: Throwable => {
            // system.log.info(s"(Level $i) Found $childrenAfterGC so far. Received exception: $ex")
            receivedValidReply = false
          } // We've timed out, so there are no more messages at this level
        }

      }

      sb.append("/*") // We go to the next level

    }

    topNode ! Contains(requester.ref, 1000, 2)
    requester.expectMsg(ContainsResult(1000, result = false))

    topNode ! Contains(requester.ref, 2000, 270)
    requester.expectMsg(ContainsResult(2000, result = true))


    // When we're all set and done, there should be 251 nodes: 250 regular nodes + 1 root
    assert(childrenAfterGC == 251)

    }


  }

  override def afterAll: Unit = system.terminate()


}