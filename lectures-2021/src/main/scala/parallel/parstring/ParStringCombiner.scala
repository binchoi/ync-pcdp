package parallel.parstring

import scala.collection.parallel.Combiner

/**
  * Example 17: ParString Combiner
  */
class ParStringCombiner extends Combiner[Char, ParString] {
  // Used as string accumulators
  import scala.collection.mutable.ArrayBuffer

  private var sz = 0

  // Buckets for separate strings
  private val chunks = {
    val buff = new ArrayBuffer[StringBuilder]
    buff += new StringBuilder
    buff
  }

  // the last chunk
  private var lastc = chunks.last

  def size: Int = sz

  // Add ellements to the last chunk
  def +=(elem: Char): this.type = {
    lastc += elem
    sz += 1
    this
  }


  // Remove all elements from the combiner
  def clear = {
    chunks.clear
    chunks += new StringBuilder
    lastc = chunks.last
    sz = 0
  }

  // Take a new  combiner
  def combine[U <: Char, NewTo >: ParString](that: Combiner[U, NewTo]): ParStringCombiner =
    if (that eq this) {
      this
    } else
    //
      that match {
        case that: ParStringCombiner =>
          // Add sizes
          sz += that.sz
          // Concatenate chunks
          chunks ++= that.chunks
          // Update the last chunk
          lastc = chunks.last
          this
      }

  // Concatenate all chunks into a string
  def result: ParString = {
    val rsb = new StringBuilder
    for (sb <- chunks) rsb.append(sb)
    new ParString(rsb.toString)
  }
}