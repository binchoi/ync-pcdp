package monitors

import org.scalatest.{FunSpec, Matchers}

import java.util.concurrent.locks.ReadWriteLock

/**
  * @author Ilya Sergey
  */
trait ReadWriteLockTest extends FunSpec with Matchers {

  def lock : ReadWriteLock

  describe(s"ReadWriteLock implementation") {

    var counter = 0
    lazy val wLock = lock.writeLock
    lazy val rLock = lock.readLock

    it("should be correct ") {
      val writer = new Writer
      val readers = (1 to 50).map(_ => new Reader)

      (writer :: readers.toList).foreach(_.start())

      (writer :: readers.toList).foreach(_.join())
      
      assert(readers.forall(r => r.result.isDefined))
      assert(readers.forall(r => r.result.get % 5 == 0))
      
      readers.foreach(r => println(s"Result: ${r.result.get}"))
    }


    class Writer extends Thread {
      override def run() = {
        for (i <- 1 to 1000) {
          wLock.lock()
          counter = counter + 1
          counter = counter + 1
          counter = counter + 1
          counter = counter + 1
          counter = counter + 1
          wLock.unlock()
          Thread.sleep(0, 1) 
        }
      }
    }

    class Reader extends Thread {
      var result: Option[Int] = None

      override def run() = {
        rLock.lock()
        result = Some(counter)
        rLock.unlock()
      }
    }

  }
}
