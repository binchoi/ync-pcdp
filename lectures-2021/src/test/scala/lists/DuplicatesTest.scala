package lists

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class DuplicatesTest extends FunSpec with Matchers {

  describe("Optimistic list") {
    it("shouldn't admit duplicates") {
      val list = new OptimisticList[Int]
      assert(list.add(42))
      assert(!list.add(42))
      assert(list.contains(42))
      list.remove(42)
      assert(!list.contains(42))
    }
  }

  describe("Lazy list") {
    it("shouldn't admit duplicates") {
      val list = new LazyList[Int]
      assert(list.add(42))
      assert(!list.add(42))
      assert(list.contains(42))
      list.remove(42)
      assert(!list.contains(42))
    }
  }
}
