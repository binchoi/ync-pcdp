package homework.problem1

/**
  * @author Ilya Sergey
  */
object Problem1 {
  
  def compose[A, B, C](g: B => C, f: A => B): A => C = {
    (x:A) => g(f(x))
  }

  // The result is an Option of a Pair
  def fuse[A, B](a: Option[A], b: Option[B]): Option[(A, B)] = {
    (a, b) match {
      case (None, b) => None
      case (a, None) => None
      case (a, b) => Some(a.get, b.get)
    }
  }

  def check[T](xs: Seq[T], pred: T => Boolean): Boolean =
    xs.forall(pred)
  
  class Pair[P,Q](val first: P, val second: Q) {
    val fst: P = first
    val snd: Q = second
  }
}
