package skiplists

import lists.GenericListTests

/**
  * @author Ilya Sergey
  */
class SynchronizedSkipListTests extends GenericListTests {
  
  override def mkSet = new SynchronizedSkipList[Int]
}
