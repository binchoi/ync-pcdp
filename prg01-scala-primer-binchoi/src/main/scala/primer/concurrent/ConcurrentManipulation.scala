package primer.concurrent

/**
  * @author Ilya Sergey
  */
object ConcurrentManipulation {

  private var workingList: List[Int] = Nil
  
  def addToList(i: Int) = {
    this.synchronized {
      workingList = i :: workingList
    }
  }

  def removeFromListWithoutSync = {
    // Notice how synchornisation is missing
    // This can cause troubles: which?
    workingList match {
      case Nil => None
      case ::(head, tl) =>
        // Wait for some time
        Thread.sleep(10)
        workingList = tl
        Some(head)
    }
  }

  def removeFromList = this.synchronized{
    workingList match {
      case Nil => None
      case ::(head, tl) =>
        // Wait for some time
        Thread.sleep(10)
        workingList = tl
        Some(head)
    }
  }


  class Adder(start: Int) extends Thread {
    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      val end = start + 9
      for (i <- start to end) {
        addToList(i)
        println(s"Thread $id: Added $i")
      }
    }
  }

  class Remover extends Thread {
    override def run() = {
      // Get my thread id
      val id = Thread.currentThread().getId
      var result: Option[Int] = None
      do {
        result = removeFromList // WithoutSync shows very different outcome
        result match {
          case Some(value) =>
            println(s"Thread $id: Removed $value")
          case None => // Do nothing
        }
      } while (result.isDefined) // Loop while the list is not depleted
    }
  }

  def main(args: Array[String]): Unit = {
    // Create two threads without executing them
    val adder1 = new Adder(1)
    val adder2 = new Adder(11)

    // Start two threads in parallel with this one
    adder1.start()
    adder2.start()

    // Wait in this thread while those two will finish
    adder1.join()
    adder2.join()

    println()

    // Make two new threads
    val remover1 = new Remover
    val remover2 = new Remover

    // Start two threads in parallel with this one
    remover1.start()
    remover2.start()

    // Wait in this thread while those two will finish
    remover1.join()
    remover2.join()
  }


}
