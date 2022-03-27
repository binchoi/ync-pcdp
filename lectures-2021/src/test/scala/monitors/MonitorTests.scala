package monitors

import monitors.counting._
import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class MonitorTests extends FunSpec with Matchers {

  describe(s"Checking implementations of monitors") {
    
    val arr = Array(false.toString)
  
    // Example 1
    it("CountTTASLock should be correct ") {
      CountTTASLock.main(arr)
    }

    // Example 2
    it("CountSingleCondition should be correct ") {
      CountSingleCondition.main(arr)
    }

    // Example 3
    it("CountMultipleConditions should be correct ") {
      CountMultipleConditions.main(arr)
    }

    // Example 4
    it("CountManyThreads should be correct ") {
      CountManyThreads.main(arr)
    }

    // Example 5
    it("CountIntrinsicMonitor should be correct ") {
      CountIntrinsicMonitor.main(arr)
    }

  }
}
