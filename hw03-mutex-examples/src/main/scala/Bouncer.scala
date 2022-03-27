import util.ThreadID

class Bouncer {

  @volatile
  private var goRight: Boolean = false
  @volatile
  private var last: Int = -1
  
  // Import all members of Direction object
  import Direction._
  
  def visit(): Value = {
    val i = ThreadID.get
    last = i
    if (goRight) {
      return RIGHT
    }
    // The "sleep" is just to make things more interesting in tests
    // Feel free to ignore it for your reasoning
    Thread.sleep(0, 1)
    goRight = true
    if (last == i) {
      STOP
    } else {
      DOWN
    }
  }


}

// We can have inner objects
object Direction extends Enumeration {
  // This is an "enumeration" object defining a fixed set of values
  type Direction = Value
  val DOWN, RIGHT, STOP = Value
}
  
