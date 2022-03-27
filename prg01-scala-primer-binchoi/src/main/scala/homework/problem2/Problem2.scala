package homework.problem2

import scala.runtime.Nothing$

/**
  * @author Ilya Sergey
  */
object Problem2 {
  
  // Higher-order functions on scala collections
  def myMap[T, S](l: List[T], f: T => S): List[S] =
    l match {
      case Nil => Nil
      case h :: t => f(h) :: myMap(t: List[T], f: T => S)
    }

  def myFilter[T](l: List[T], f: T => Boolean): List[T] =
    l match {
      case Nil => Nil
      case h :: t => if (f(h)) {
        (h :: myFilter(t, f))}
      else {
        myFilter(t, f)}
      }

  // Flattening the list
  def myFlatten[T](l: List[List[T]]): List[T] = {
    var res = List(): List[T]
    for (i <- l.indices) {
      if (l(i).isEmpty) {None} else {
        for (j <- l(i).indices) {
          res = l(i)(j) :: res
        }
      }
    }
    res
  }

  def myFoldLeft[A, B](l: List[A], z: B, f: B => A => B): B =
    l match {
      case Nil => z
      case h :: t => myFoldLeft(t, f(z)(h), f)
    }

  def myFoldRight[A, B](l: List[A], z: B, f: A => B => B): B =
    l match {
      case Nil => z
      case h :: t => f(h)(myFoldRight(t, z, f))
    }
}
