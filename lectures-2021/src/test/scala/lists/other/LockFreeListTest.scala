package lists.other

import lists.{GenericListTests, LockFreeList}

/**
  * @author Ilya Sergey
  */
class LockFreeListTest extends GenericListTests {
  override def mkSet = new LockFreeList[Int]
}
