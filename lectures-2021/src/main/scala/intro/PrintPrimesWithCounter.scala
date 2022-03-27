package intro

/**
  * @author Ilya Sergey
  * 
  * A counter-based printer
  * 
  * Questions:
  * 1. What's with the large numbers?
  * 2. Is everything okay with the counter?
  */
object PrintPrimesWithCounter extends PrimePrinter {
  
  val counter = new Counter

  def primePrint(powerOfTen: Int): Unit = {
    var i: Int = 1
    val limit = math.pow(10, powerOfTen).intValue
    while (i < limit) {
      i = counter.getAndIncrement
      if (i < limit && PrimeNumbers.isPrime(i)) {
        println(i)
      }
    }
  }

}
