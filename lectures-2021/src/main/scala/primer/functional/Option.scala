package primer.functional

/**
  * @author Ilya Sergey
  */
sealed trait Option[+T]

case class Some[T](elem: T) extends Option[T]

case object None extends Option[Nothing]

object PrintOption {
  
  def printOption(o: Option[String]): Unit = {
    o match {
      case Some(elem) => println(elem)
      case None => println("Nothing")
    }
  }

  def main(args: Array[String]): Unit = {
    printOption(Some("PCDP"))
    printOption(None)
  }
  
}