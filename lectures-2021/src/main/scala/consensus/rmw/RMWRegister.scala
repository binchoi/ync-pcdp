package consensus.rmw

/**
  * Read-Modify-Write Register
  *
  * @author Ilya Sergey
  */
class RMWRegister(private val init: Int) {
  private var value: Int = init

  /*
      def getAndMumble() = this.synchronized {
        val prior = value
        value = mumble(value)
        prior
      }
  */

  def read: Int = this.synchronized {
    val prior = value
    value = value
    prior
  }

  def getAndSet(v: Int): Int = this.synchronized {
    val prior = value
    value = v
    prior
  }

  def getAndIncrement: Int = this.synchronized {
    val prior = value
    value = value + 1
    prior
  }

  def getAndAdd(a: Int): Int = this.synchronized {
    val prior = value
    value = value + a
    prior
  }

  def compareAndSet(expected: Int, update: Int): Boolean =
    this.synchronized {
      if (value == expected) {
        value = update
        true
      } else {
        false
      }
    }


}
