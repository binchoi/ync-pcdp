package parallel.parstring

import scala.collection.immutable.Seq
import scala.collection.parallel.immutable.ParSeq

/**
  * Example 13: Implementation of a parallel string
  */
class ParString(val str: String) extends ParSeq[Char] {

  def apply(i: Int): Char = str.charAt(i)

  def length: Int = str.length

  def seq: Seq[Char] = new collection.immutable.WrappedString(str)

  def splitter = new ParStringSplitter(str, 0, str.length)

  override def newCombiner = new ParStringCombiner

}
