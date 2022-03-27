package lists.other

import lists.{GenericListTests, LazyList}

/**
  * @author Ilya Sergey
  */
class LazyListTest extends GenericListTests {
  override def mkSet = new LazyList[Int]
}
