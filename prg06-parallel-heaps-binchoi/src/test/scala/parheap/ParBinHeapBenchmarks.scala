package parheap

import scala.collection.mutable

object ParBinHeapBenchmarks {

  def main(args: Array[String]): Unit = {

    val ARR_SIZE = 1000000
    val arr = ArrayUtil.generateRandomStringArray(ARR_SIZE, 100)

    val seqHeap = new mutable.PriorityQueue[String]
    val parHeap = new ParBinHeap[String](ARR_SIZE)

    arr.foreach(seqHeap.enqueue(_))
    arr.foreach(parHeap.insert)

    val count_seq = warmedTimed() {
      seqHeap.count(_.contains("aa"))
    }

    val count_par = warmedTimed() {
      parHeap.count(_.contains("aa"))
    }

    println(s"Performance of count ($ARR_SIZE elements): ")
    println(s"Sequential: $count_seq ms")
    println(s"Parallel  : $count_par ms")
    println()

    val aggregate_seq = warmedTimed() {
      seqHeap.aggregate[List[String]](Nil)((acc, str) => if(str.contains("aa")) str :: acc else acc, (acc1, acc2) => acc1 ++ acc2)
    }

    val aggregate_par = warmedTimed() {
      parHeap.aggregate[List[String]](Nil)((acc, str) => if(str.contains("aa")) str :: acc else acc, (acc1, acc2) => acc1 ++ acc2)
    }

    println(s"Performance of aggregate ($ARR_SIZE elements):")
    println(s"Sequential: $aggregate_seq ms")
    println(s"Parallel  : $aggregate_par ms")
    println()

    val fold_seq = warmedTimed() {
      seqHeap.fold("")((acc, str) => if(str.contains("aaaa")) str ++ acc else acc)
    }

    val fold_par = warmedTimed() {
      parHeap.fold("")((acc, str) => if(str.contains("aaaa")) str ++ acc else acc)
    }

    println(s"Performance of fold ($ARR_SIZE elements):")
    println(s"Sequential: $fold_seq ms")
    println(s"Parallel  : $fold_par ms")
    println()

  }

}
