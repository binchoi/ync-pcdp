package monitors

import util.ThreadID

/**
  * @author Ilya Sergey
  */
trait Logging {

  var printLog: Boolean = true
  
  def println(s: String) = if (printLog) System.out.println(s)
  
  def println() = if (printLog) System.out.println()

  def main(args: Array[String]): Unit = {
    ThreadID.reset()
    if (args.length > 0) {
      printLog = args(0).toBoolean
    }
  }


}
