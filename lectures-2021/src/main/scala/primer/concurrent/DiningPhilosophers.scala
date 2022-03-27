package primer.concurrent

/**
  * @author Ilya Sergey
  */
object DiningPhilosophers {

  def main(args: Array[String]): Unit = {

    val numPhil = 5

    for (j <- 1 to 10) {
      println(s"Dinner No. $j")
      arrangeDinner(numPhil)
      println(s"Dinner No. $j is complete!\n")
    }
  }


  private def arrangeDinner(num: Int): Unit = {
    val philosophers = new Array[Philosopher](num)
    // Array of forks [1 ... numPhil]
    val forks = (for (i <- 1 to philosophers.length) yield new Integer(i)).toArray
    val ts = for (i <- philosophers.indices) yield {
      val leftFork = forks(i)
      val rightFork = forks((i + 1) % forks.length)
      if (i < philosophers.length - 1) {
        philosophers(i) = new Philosopher(leftFork, rightFork)
      } else {
        philosophers(i) = new Philosopher(rightFork, leftFork)
      }
      new Thread(philosophers(i), s"Philosopher ${i + 1}")
    } 
    
    // Start all threads
    ts.foreach(t => t.start())
    
    // Wait until all they done
    ts.foreach(t => t.join())
    
    // TODO: Implement me!
  }

  class Philosopher(var leftFork: AnyRef, var rightFork: AnyRef) extends Runnable {

    @throws[InterruptedException]
    private def doAction(action: String): Unit = {
      System.out.println(Thread.currentThread.getName + " " + action)
      Thread.sleep((Math.random * 100).toInt)
    }

    def run(): Unit = {
      // thinking
      doAction(": Thinking")

      // TODO: Pick left fork
      leftFork.synchronized {
        doAction(s": Picked up left fork $leftFork")

        // TODO: Pick right work
        rightFork.synchronized {

          // eating
          doAction(s": Picked up right fork $rightFork - eating")
          doAction(s": Put down right fork $rightFork")

          // Back to thinking
          doAction(s": Put down left fork $leftFork. Done now.")
        }
      }
    }

  }

}