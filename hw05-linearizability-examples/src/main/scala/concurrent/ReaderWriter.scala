package concurrent

/**
  * @author Ilya Sergey
  */
class ReaderWriter {
  var x: Int = 0
  @volatile
  var v: Boolean = false

  def writer(): Unit = {
    x = 42
    v = true
  }

  def reader(): Unit = {
    if (v) {
      var y = 100 / x
      x = 0
      v = !(y >= 0)
    }
  }

}
