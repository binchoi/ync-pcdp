package examples

/**
  * An example of a graceful shutdown of a thread
  * 
  * @author Ilya Sergey
  */
object InterruptThreadExample {
  
  // Just an alias for this object for convenience
  val monitor = this

  def main(args: Array[String]): Unit = {
    
    val t = new Thread {
      override def run() = {
        val i = Thread.currentThread().getId()
        
        try {
          monitor.synchronized {
            println(s"Thread $i is about to block.")
            while (true) {
              // Wait forever here
              monitor.wait()
            }
          }
        } catch {
          case e: InterruptedException =>
            // The thread has received an interrupt call
            println(s"Thread $i has been interrupted, terminating now.")
        }
      }
    }
    
    t.start()
    
    // Wait a bit
    Thread.sleep(500)
    
    val j = Thread.currentThread().getId()
    
    println(s"Thread $j is about to interrupt thread ${t.getId}.")
    
    // TODO: What is going to happen if we don't call this?
    t.interrupt()
    
  }

}
