package lists

/**
  * @author Ilya Sergey
  */
class CoarseListTest extends GenericListTests {
  override def mkSet = new CoarseList[Int]
}
