package concurrent

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class RWTest extends FunSpec with Matchers {

  describe("Reader-writer") {
    it("should never divide by zero") {
      val LIMIT = 10
      val v = new ReaderWriter()
      var result = true
      
      val writer = new Thread() {
        override def run() = {
          for (i <- 1 to LIMIT) {
            v.writer()
          }
        }
      }
      val reader = new Thread() {
        override def run() = {
          for (i <- 1 to LIMIT) {
            try {
              v.reader()
            } catch {
              // Division by Zero happened
              case _: ArithmeticException => result = false
            }
          }
        }
      }
      
      writer.start()
      reader.start()
      writer.join()
      reader.join()
      // Nothing bad is going to happen, right?
      assert(result)
    }

  }
}