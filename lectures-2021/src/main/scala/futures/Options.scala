package futures

/**
 * Example 9: Options as Monads
 *
 * @author Ilya Sergey
 */
object Options {

  // TODO: let's try 3 different solutions

  def printInner1[T](a: Option[T]): Unit =
    a match {
      case Some(value) => println(value)
      case None =>
    }

  def printInner2[T](a: Option[T]): Unit =
    a.foreach(println(_)) 
  


  def printInner3[T](a: Option[T]): Unit = 
    for (e <- a) { println(e) } 


  ////////////////////////////////////////////////////
  ////////////////////////////////////////////////////


  // TODO: let's try 3 different solutions
  def mapOption1[T, S](a: Option[T], f: T => S): Option[S] =
    a match {
      case Some(value) => Some(f(value))
      case None => None
    }

  def mapOption2[T, S](a: Option[T], f: T => S): Option[S] =
    a.map(f)

  def mapOption3[T, S](a: Option[T], f: T => S): Option[S] = 
    for (e <- a) yield f(e)

  ////////////////////////////////////////////////////
  ////////////////////////////////////////////////////

  // TODO: also 3 different solutions for "bind" two options
  def blend1[T, S](a: Option[T], b: Option[S]): Option[(T, S)] = {
    (a, b) match {
      case (Some(x), Some(y)) => Some((x, y))
      case _ => None
    }
  }

  // TODO: Flatten is known as "bind"
  def blend2[T, S](a: Option[T], b: Option[S]): Option[(T, S)] = {
    a.flatMap(xa => b.flatMap(xb => Some((xa, xb))))
    // flatMap = bind
    // x >>= f
  }


  def blend3[T, S](a: Option[T], b: Option[S]): Option[(T, S)] = 
    for (xa <- a; 
         bx <- b) yield (xa, bx)
    
  
  ////////////////////////////////////////////////////
  ////////////////////////////////////////////////////

  def main(args: Array[String]): Unit = {
    val some = Some(1)
    val some2 = Some(2)
    val none = None

    assert(blend1(some, some2) == blend2(some, some2))
    assert(blend1(some, some2) == blend3(some, some2))

    //    printInner1(some)
    //    printInner2(some)
    //    printInner3(some)
    //
    //    printInner1(none)
    //    printInner2(none)
    //    printInner3(none)

  }


}
