package intro

/**
  * @author Ilya Sergey
  */
object PrimeNumbersSequential extends PrimePrinter {

  override def primePrint(powerOfTen: Int): Unit = {
    val limit = math.pow(10, powerOfTen).intValue
    for (j <- 1 to limit) {
      if (PrimeNumbers.isPrime(j)) {
        println(j)
      }
    }
  }

  override def main(args: Array[String]): Unit = {

    val t0 = System.currentTimeMillis()
    primePrint(6)
    val t1 = System.currentTimeMillis()

    println("Elapsed time: " + (t1 - t0) + " ms")
  }

}
