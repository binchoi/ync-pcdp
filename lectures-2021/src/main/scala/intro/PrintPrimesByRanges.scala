package intro

import util.ThreadID

/**
  * @author Ilya Sergey
  * 
  * A range-based implementation of prime printer
  */

object PrintPrimesByRanges extends PrimePrinter {

  def primePrint(powerOfTen: Int): Unit = {
    val i = ThreadID.get
    val block = math.pow(10, powerOfTen - 1).intValue
    for (j <- (i * block) + 1 to (i + 1) * block) {
      if (PrimeNumbers.isPrime(j)) {
        println(j)
      }
    }
  }

}


