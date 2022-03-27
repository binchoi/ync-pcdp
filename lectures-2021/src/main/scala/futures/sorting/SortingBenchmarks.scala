package futures.sorting

/**
  * @author Ilya Sergey
  */
object SortingBenchmarks {

  val scalaSorter = ScalaSort
  val hybridSorter = FutureSort

  def main(args: Array[String]): Unit = {

    // Running for size 10000
    runComparison(100000)

    // Running for size 1000000
    runComparison(1000000)

    // Running for size 10000000
    runComparison(10000000)

    // Only if you feel brave and have time for this...
    // Running for size 20000000
    // runComparison(20000000)

  }

  private def runComparison(size: Int) = {
    println(s"Running benchmarks for array size $size")
    val arr = ArrayUtil.generateRandomArrayOfInts(size)

    runBenchmark(scalaSorter, arr)
    runBenchmark(hybridSorter, arr)
    println()
  }

  private def runBenchmark(sorter: Sorting, arr: Array[Int]) = {
    // Let's make sure we won't mess up the original array
    val a = ArrayUtil.arrayCopy(arr)
    print(s"${sorter.getName}: ")
    val t1: Long = System.currentTimeMillis()
    sorter.sort(a)
    val t2: Long = System.currentTimeMillis()
    val formatter = java.text.NumberFormat.getIntegerInstance
    println(s"${formatter.format(t2 - t1)} ms")

    //    assert(ArrayUtil.checkSorted(a))
    //    assert(ArrayUtil.checkSameElements(arr, a))
  }
}
