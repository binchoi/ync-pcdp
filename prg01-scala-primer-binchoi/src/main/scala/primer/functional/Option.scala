package primer.functional

/**
  * @author Ilya Sergey
  */
trait Option[+T]

case class Some[T](elem: T) extends Option[T]

case object None extends Option[Nothing]

object PrintOption {
  
  def printOption(o: Option[String]): Unit = {
    o match {
      case Some(e) => 
        println("Some: " + e) 
      case None => 
        println("Nothing!")
    }
  }

  def main(args: Array[String]): Unit = {
    val s = Some("Hello")
    printOption(s)
  }
  
}