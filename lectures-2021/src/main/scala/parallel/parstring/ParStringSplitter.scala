package parallel.parstring

import scala.collection.parallel.SeqSplitter

/**
  * Example 14: ParString Splitter
  */
class ParStringSplitter(private val s: String, 
                        private var i: Int, 
                        private val limit: Int)
  extends SeqSplitter[Char] {

  // Same functionality as of the iterator
  final def hasNext = i < limit

  // Same functionality as of the iterator
  final def next = {
    val r = s.charAt(i)
    i += 1
    r
  }

  // How many elements remains in the collection
  def remaining = limit - i

  // Duplicate the splitter
  def dup = new ParStringSplitter(s, i, limit)

  // A general method that splits collection according to a vector of sizes
  // See the documentation in `PreciseSplitter`
  def psplit(sizes: Int*): Seq[ParStringSplitter] = {
    // creating splitters
    val ss = for (sz <- sizes) yield {
      val nlimit = math.min(i + sz, limit)
      val ps = new ParStringSplitter(s, i, nlimit)
      i = nlimit
      ps
    }
    
    // Exhausted the entire collection
    if (i == limit) {
      ss
    } else {
      // Adding a new element
      ss :+ new ParStringSplitter(s, i, limit)
    }
  }

  // Just splitting into two parts
  def split: Seq[ParStringSplitter] = {
    val rem = remaining
    if (rem >= 2) psplit(rem / 2, rem - rem / 2)
    else Seq(this)
  }
}