package primer.objects

/**
 * @author Ilya Sergey
 */
trait Logging {
  // Signature of a method to be defined later 
  def log(s: String): Unit

  def warn(s: String) = log("WARN: " + s)

  def error(s: String) = log("ERROR: " + s)
}


// A class that mixes in Logging's functionality
class PrintLogging extends Logging {
  def log(s: String) = println(s)
}

object UseLogging extends App {
  val logger = new PrintLogging
  logger.warn("Hmm...")
}


