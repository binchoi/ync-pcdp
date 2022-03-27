package intro

/**
  * @author Ilya Sergey
  */
object PrimeNumbers {
  
  def isPrime(i: Int): Boolean = {
    if (i <= 0) return false
    if (i <= 2) return true 
    
    for (j <- 2 to math.sqrt(i).toInt) {
      if (i % j == 0) return false
    }
    true
  }

}
