package primer.functional

/**
 * @author Ilya Sergey
 */
object FunctionalElements1 {

  val twice: Int => Int = (x: Int) => x * 2

  def runTwice(body: => Unit): Unit = {
    body
    body
  }

  def main(args: Array[String]): Unit = {
    // This will print Hello twice.
    runTwice({
      println("Hello!")
    })
  }

}

object FunctionalElements2 {

  def main(args: Array[String]): Unit = {
    val ls = List(1, 2, 3, 4)
    for (e <- ls) {
      println(e)
    }

    println(ls.map(x => x * 3))
  }

}

object FunctionalElements3 extends App {

  val messages : Seq[String] = Seq("Hello", "World", "!")
  val messageMap : Map[Int, String] = Map(1 -> "Hello", 2 -> "World", 3 -> "!")

  // Convert all elements of `messages` to strings (which they already are), 
  // concatenate with the " " as a separator and print the result:
  println(messages.mkString(" "))

  //Create a new map by adding a key-value pair (4 -> "Yay") to messageMap
  println(messageMap + (4 -> "Yay"))

}
