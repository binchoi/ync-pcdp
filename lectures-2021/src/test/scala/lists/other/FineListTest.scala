package lists.other

import lists.{FineList, GenericListTests}

/**
  * @author Ilya Sergey
  */
class FineListTest extends GenericListTests {
  override def mkSet = new FineList[Int]
}
