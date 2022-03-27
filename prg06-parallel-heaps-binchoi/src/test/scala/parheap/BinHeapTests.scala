package parheap

import org.scalatest.{FunSpec, Matchers}

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class BinHeapTests extends FunSpec with Matchers {


  describe(s"Parallel heap implementation") {
    it("should correctly  return `min`") {
      val h1 = new ParBinHeap[Int](10)
      h1.insert(10)
      h1.insert(2)
      h1.insert(1)
      assert(h1.removeMin==1)
      assert(h1.removeMin==2)
      assert(h1.removeMin==10)
    }

    it("should not lose elements") {
      val h1 = new ParBinHeap[Int](1000)
      for (i <- 1 to 10) {
        h1.insert(i)
      }
      assert(h1.size == 10)

      for (i <- 1 to 990) {
        h1.insert(i)
      }
      assert(h1.size == 1000)

      for (i <- 1 to 500) {
        h1.removeMin
      }
      assert(h1.size == 500)
    }


    it("should return elements in the increasing order") {
      // TODO: Implement me!
      val h1 = new ParBinHeap[Int](10)
      h1.insert(1)
      h1.insert(3)
      h1.insert(2)
      h1.insert(5)
      h1.insert(8)
      h1.insert(10)
      h1.insert(0)
      h1.insert(-1)
      h1.insert(-2)
      h1.insert(100)

//      var num = 0
//      for (i <- h1.getArray()) {
//        println(i, s" - index: $num")
//        num += 1
//      }

      val min_remove_list = List.fill(10)(h1.removeMin)
      assert(min_remove_list == min_remove_list.sorted)

      (9 to 0 by -1).foreach(num => h1.insert(num))
      val min_remove_list2 = List.fill(10)(h1.removeMin)

      assert(min_remove_list2 == min_remove_list2.sorted)
    }
    
    // Do not modify this test!
    it("should correctly return `max` when run with a reversed ordering") {
      // Do not remove this one, otherwise you'll have troubles when implementing the rest of the test!
      implicit val intTag = new ClassTag[Int] {
        override def runtimeClass = Int.getClass
      }

      val arr = ArrayUtil.generateRandomArrayOfInts(100000)
      val heap = new ParBinHeap[Int](arr)((x: Int, y: Int) => y - x, intTag)
      assert(heap.removeMin == arr.max)
    }


  }


}
