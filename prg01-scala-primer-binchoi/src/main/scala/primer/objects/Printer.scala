package primer.objects

/**
  * @author Ilya Sergey
  */
class Printer(val initial: String) {
  private var greeting = initial

  def printMessage(): Unit = println(greeting + "!")

  def printNumber(x: Int): Unit = {
    println("Number: " + x)
  }
  
  def setGreeting(s: String) = {
    greeting = s
  }
}

object UsePrinter {
  def main(args: Array[String]): Unit = {
    val printer = new Printer("Hello")
    printer.printMessage()
    printer.printNumber(42)
  }
}