package monitors.exchange

/**
  * Exercise: Exchange via a monitor
  */
object WordProcessor {

  /* TODO: Implement a 2-thread word processor
  
  1. Thread `InputReader` reads user input from the console.
     Use `scala.io.StdIn.readLine()` to read a single line from input.
     
  2. InputReader stores each of the input lines into a new slot 
     in the shared array `candidates`. 
     
  3. Thread `InputStatsPrinter` reads the user inputs from `candidates`. 
     If there are no new slots, the thread is inactive. 
     If a new input is available, it prints the message "Input i size: N",
     where `i` is a number of the input and `N` is its size as a string.
     
  4. You can use either intrinsic Java monitors (which object will serve as a monitor?) 
     or `ReentrantLock` and its `newCondition()` method to implement it.
     
  5. Bonus: try to reduce the amount of synchronisation between the threads 
     (i.e., make critical sections as small as possible)           
   */

  val NUM = 5   // How many words do we read:
  
  val candidates: Array[Option[String]] = Array.fill(NUM)(None)   // An array for storing user inputs

  class InputReader extends Thread {
    
    override def run() = {
      for (i <- 0 until NUM) {
        val line = scala.io.StdIn.readLine()
        // TODO: Implement synchronization here.
      }
    }
  }

  class InputStatsPrinter extends Thread {
    override def run() = {
      var s : String = null
      for (i <- 0 until NUM) {
        print("Enter something: ")
        // TODO: Implement synchronization here.
        println(s"Input ${i + 1} size: ${s.length}")
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val t1 = new InputReader
    val t2 = new InputStatsPrinter

    t1.start()
    t2.start()

    t1.join()
    t2.join()
  }

}
