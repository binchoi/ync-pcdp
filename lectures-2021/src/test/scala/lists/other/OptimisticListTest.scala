package lists.other

import lists.{GenericListTests, OptimisticList}

/**
  * @author Ilya Sergey
  */
class OptimisticListTest extends GenericListTests {
  override def mkSet = new OptimisticList[Int]()
}
