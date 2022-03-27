package futures

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.concurrent.{Executors, TimeUnit}
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.Success

/** Problem 3:

Solve the following problem using Scala's Futures and Promises. 
For a range of bit integers, find the first prime number in it that is a palindrome, 
or return -1 if no such number exists. To do so, split the range into sub-ranges that
will be searched concurrently by tasks enclosed into Futures. 
To implement cancellation, use a promise: all tasks should check from time to time if the
promise is completed, in which case they should stop their attempts.

Some hints:

* The parameter `workers` determines how many Futures running concurrently should be allocated

* Use `trySuccess` and `tryFailure` of the `Promise` for CAS-like installing of a result into the promise

* A good concurrent HashMap is provided by Scala's TrieMap     

* Think when the palindrome-seeking futures need to synchronise with each other.
  For instance, can futures for "later" ranges announce the results before "earlier" ones?

* Use `BigInt(x).isProbablePrime(10)` to check a number of primality with high probability

* Do not use explicit awaits in the futures!

* That said, a bit of spinning in individual futures (on some variable to be updated) is okay.

 */
object PalindromeSearch {

  private implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  def firstPalindromePrime(from: Int, upTo: Int,
                           workers: Int = Runtime.getRuntime.availableProcessors()): Future[Int] = {
    val pool = Executors.newFixedThreadPool(workers)
    val p = Promise[Int] // Represents the FOUND palindromePrime!

    var Loss = false
    val f = p.future
    val counter = new AtomicInteger(from)
    var done = new AtomicBoolean(false)

    while (!p.isCompleted & counter.get()<=upTo) {
      val fut = pool.submit(new Runnable {
        override def run(): Unit = {
          val myNum = counter.getAndAdd(1)
          if (myNum > upTo) {
            return
          }
          if (BigInt(myNum).isProbablePrime(10) & (myNum.toString.reverse == myNum.toString)) {
            if (!p.isCompleted) {
              p.trySuccess(myNum)
            }
            if (p.isCompleted & Loss) { // overwrite!
              p.trySuccess(myNum)
              Loss = false
            }
          }
          if (counter.get > upTo) {
            done.getAndSet(true)
          }
        }
      })
    }

    while (! done.get()) {}

    if (!p.isCompleted) {
      Loss = true
      p.trySuccess(-1)
    }

    val resMachine = Future {
      println("Waiting for the result:\n")
    }.flatMap { _ => f }

    val res = resMachine
    pool.shutdown()
    res
  }

  def main(args: Array[String]): Unit = {
    // Use this for testing
    val search = firstPalindromePrime(1, 3)
    val r = Await.result(search, Duration.create(10, TimeUnit.SECONDS))
    println(r)
  }
}
