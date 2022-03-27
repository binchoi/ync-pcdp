package primer.runners.squares

/**
  * @author Ilya Sergey
  */
object SquareOf5 extends App {
  def square(x: Int) = x * x
  val s = square(5)
  println(s"Result $s")
}
