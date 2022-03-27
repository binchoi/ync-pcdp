package futures

import futures.PalindromeSearch.firstPalindromePrime
import org.scalatest.AsyncFlatSpec

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * @author Ilya Sergey
  */
class PalindromeTests extends AsyncFlatSpec {
  
  behavior of "PalindromeSearch"
  it should "correctly find palindromes in range where they exist" in {
    // TODO: Implement me!
    //       Please, try some large numbers

    val search = firstPalindromePrime(728, 780)
    search.map(res =>
    assert(res == 757))
    
  }

  it should "Report failure if there is no palindrome" in {
    // TODO: Implement me!

    val search = firstPalindromePrime(3998, 4003)
    search.map(res =>
      assert(res == -1))


  }

  it should "Report failure if there is no palindrome 3" in {

    for{
      res1 <- PalindromeSearch.firstPalindromePrime(8, 10)
      res2 <- PalindromeSearch.firstPalindromePrime(60, 70)
      res3 <- PalindromeSearch.firstPalindromePrime(14800, 15200)
      res4 <- PalindromeSearch.firstPalindromePrime(100030002, 100050000)
      res5 <- PalindromeSearch.firstPalindromePrime(100000000, 100030000)
    } yield {
      assert(res1 == -1)
      assert(res2 == -1)
      assert(res3 == -1)
      assert(res4 == -1)
      assert(res5 == -1)
    }
  }


}
