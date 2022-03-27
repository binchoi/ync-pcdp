package util

/** 
    This class will be useful in having your threads receive unique thread-local 
    IDs starting from 0. 

    The reason we give this to you is because by the time you are able to create
    threads in your program, the JVM has already spawned several threads of its 
    own, thus reserving the first few thread IDs. Thus if you do:
        Thread.currentThread().getId()
    Your first thread will probably get some value around 8 or 9, though in
    actuality this is arbitrary (and either way not what you'd probably want).
    
    @author Maurice Herlihy, Nir Shavit, Ilya Sergey
*/

object ThreadID {
  @volatile
  private var nextID: Int = 0

  private class ThreadLocalID extends ThreadLocal[Int] {
    override protected def initialValue: Int = this.synchronized {
      val tmp = nextID 
      nextID = tmp + 1
      tmp
    }
  }

  private val threadID = new ThreadID.ThreadLocalID

  def get: Int = threadID.get

  def set(index: Int): Unit = {
    threadID.set(index)
  }
  
  def reset(): Unit = {
    nextID = 0
  }
}
