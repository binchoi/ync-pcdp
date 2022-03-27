package parheap

import org.scalatest.{FunSpec, Matchers}

import java.util.concurrent.atomic.AtomicInteger
import scala.util.{Failure, Success, Try}

/**
  * @author Ilya Sergey
  */
class ParBinHeapSplitterTests extends FunSpec with Matchers {

  private val ARR_SIZE = 1000000

  // Ensure we have some repeats (~10 repeats per num)
  private val arr = ArrayUtil.generateRandomArrayOfBoundedInts(ARR_SIZE, ARR_SIZE / 10) 
  private val heap = new ParBinHeap[Int](arr)

  describe(s"Parallel heap implementation") {

    it("should correctly `count` - let's try 100!") {
      // TODO: Implement me!
      val myRes = heap.count(_==100)
      val expRes = arr.count(_==100)
      assert(myRes==expRes)
    }

    it("should correctly `count`  - prof's test case") {
      val item = arr(ArrayUtil.generateRandomIntBetween(0, ARR_SIZE - 1))
      assert(heap.count(_ == item) == arr.count(_ == item))
    }


    it("should correctly find `max`") {
      // TODO: Implement me!
      import Ordering.Implicits._

      val myMax = heap.fold(0)(Seq(_,_).max)
      val myMax2 = heap.max
      val expMax = arr.max

      assert(myMax == expMax)
      assert(myMax2 == expMax)
    }

    it("should correctly find `max` - 2") {
      assert(heap.max == arr.max)
    }

    it("should correctly implement custom operations based on splitting") {
      val seq_aggregate = arr.aggregate[Int](0)(
        (acc, n) =>
          if(n.toString.contains("4231")) n + acc else acc
        , (acc1, acc2) =>
          acc1 + acc2
      )

      val par_aggregate = heap.aggregate[Int](0)(
        (acc, n) =>
          if(n.toString.contains("4231")) n + acc else acc
        ,
        (acc1, acc2) =>
          acc1 + acc2
      )

      assert(seq_aggregate == par_aggregate)
    }

    /**
      * This test checks that your splitter respects the heap structure.
      * Specifically, it verifies that all splitters produce sub-heaps where
      * the first element (from the left) is the minimum element of the sub-heap.
      *
      * Do not modify this test!
      */
    it("should preserve the structure of the heap") {

      val splitMin = heap.aggregate[Try[Option[Int]]](Success(None))( //SOP
        (acc, n) => {
          acc match {
            case Success(Some(min)) =>
            if (n < min) Failure(new IllegalStateException(s"Encountered minimum element in non-head position n=$n, min=$min"))
            else Success(Some(min))
            case Success(None) =>
            Success(Some(n))
            case Failure(f) =>
            Failure(f)
          }
        }, // COP
        (acc1, acc2) => {
          for {
            acc1_s <- acc1
            acc2_s <- acc2
          } yield {

            for {
              acc1_sn <- acc1_s
              acc2_sn <- acc2_s
            } yield {
              Seq(acc1_sn, acc2_sn).min
            }

          }
        }
      )

      splitMin match {
        case Success(Some(min)) =>
        assert(min == arr.min)
        case Success(None) =>
        assert(false, "minimum should not be None")
        case Failure(f) =>
        assert(false, s"encountered exception: $f")

      }

    }

  }

}
//      val mySplitter = new ParBinHeapSplitter(Array(-10, 0, 10, 20, 30, 40), 6, 0, 6)
//      while (mySplitter.hasNext) {
//        println(mySplitter.next())
//      }
//      println("splitter is exhausted of elements")
//
//      val arr1 = Array(1,2,3,4,5,6,7,8,9,10,11,12,13) //ArrayUtil.generateRandomArrayOfBoundedInts(100, 5)
//      val heap1 = new ParBinHeap[Int](arr1)
//      val res = heap1.count(_==12)
//      println(res)

//      arr1.foreach(println)
//      println(s"$res, vs. ${arr1.count(_==1)}")
//      heap.count(_ == 10)
