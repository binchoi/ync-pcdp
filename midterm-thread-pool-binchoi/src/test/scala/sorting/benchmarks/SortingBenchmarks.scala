package sorting.benchmarks

import sorting._

import scala.util.Sorting

/**
  * @author Ilya Sergey
  */
object SortingBenchmarks {

  val scalaSorter = new Sorting {
    override def sort[T: Ordering](a: Array[T]): Unit = Sorting.quickSort(a)
    override def getName = "ScalaSort "
  }

  val simpleSorter = SimpleQuickSort
  val pooledSorter = PooledQuickSort
  val hybridSorter = HybridQuickSort

  def main(args: Array[String]): Unit = {
    println("Warming up...")
    runComparison(1000000, warmed = false)
    println("Warmed up, now benchmarking:")
    println()

    // Running for size 10000
    runComparison(100000)

    // Running for size 1000000
    runComparison(1000000)

    // Running for size 10000000
    runComparison(10000000)

    // Only if you feel brave and have time for this...
    // Running for size 20000000
    runComparison(20000000)

  }

  private def runComparison(size: Int, warmed : Boolean = true) = {
    if (warmed) {
      println(s"Running benchmarks for array size $size")
    }
    val arr = ArrayUtil.generateRandomArrayOfInts(size)

    runBenchmark(scalaSorter, arr, warmed)
    runBenchmark(simpleSorter, arr, warmed)
    runBenchmark(pooledSorter, arr, warmed)
    runBenchmark(hybridSorter, arr, warmed)
    if (warmed) {
      println()
    }
  }

  private def runBenchmark(sorter: Sorting, arr: Array[Int], warmed: Boolean = true) = {
    // Let's make sure we won't mess up the original array
    val a = ArrayUtil.arrayCopy(arr)
    if (warmed) {
      print(s"${sorter.getName}: ")
    }
    val t1: Long = System.currentTimeMillis()
    sorter.sort(a)
    val t2: Long = System.currentTimeMillis()
    if (warmed) {
      val formatter = java.text.NumberFormat.getIntegerInstance
      println(s"${formatter.format(t2 - t1)} ms")
    }
  }
}

