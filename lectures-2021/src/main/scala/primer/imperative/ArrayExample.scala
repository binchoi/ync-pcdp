package primer.imperative

/**
 * @author Ilya Sergey
 */
object ArrayExample {

  var index = 0
  val nextIndex = () => {
    val tmp = index
    index = index + 1
    tmp
  }

  def main(args: Array[String]): Unit = {

    // Create an array of booleans of size 5
    val arr1 = new Array[Boolean](5)

    // Print all elements of the array
    for (i <- 0 to arr1.length - 1) {
      println(arr1(i))
    }

    // Create an array of size 5 filled by repeating the computation
    // passed as a second parameter
    val arr2 = Array.fill(5)({ nextIndex() * nextIndex() })

    // Print all elements of this array
    for (i <- arr2.indices) {
      println(arr2(i))
    }
  }
}
