package primer.objects

/**
 * @author Ilya Sergey
 */
class Pair[P, Q](val first: P, val second: Q)

object UsePair extends App {
  val p = new Pair("abc", 42)
  println(p.first)
  println(p.second)
}