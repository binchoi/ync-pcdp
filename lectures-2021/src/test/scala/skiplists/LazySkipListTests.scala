package skiplists

import lists.GenericListTests

/**
  * @author Ilya Sergey
  */
class LazySkipListTests extends GenericListTests {
  override def mkSet = new LazySkipList[Int]
}
