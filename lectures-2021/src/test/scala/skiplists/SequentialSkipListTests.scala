package skiplists

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class SequentialSkipListTests extends FunSpec with Matchers {
  
  val input = (0 to 10000).toList
  
  
  describe(s"Sequential SkipList") {
    it("should behave correctly as a set wrt. addition") {
      val set = new SequentialSkipList[Int]
      
      for (e <- input) {
        set.add(e)
      }
      
      assert(input.forall(set.contains))
      assert(!set.contains(input.max + 1))
    }
  }

  describe(s"Sequential SkipList") {
    it("should behave correctly as a set wrt. removal") {
      val set = new SequentialSkipList[Int]

      for (e <- input) {
        set.add(e)
      }
      
      for (e <- input) {
        assert(set.remove(e))
      }

      for (e <- input) {
        assert(!set.contains(e))
      }

    }

  }
}