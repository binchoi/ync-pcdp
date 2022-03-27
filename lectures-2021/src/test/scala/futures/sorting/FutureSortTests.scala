package futures.sorting

/**
  * @author Ilya Sergey
  */
class FutureSortTests extends SortingTests {

  override val ARRAY_SIZE: Int = 5000
  
  val sorter = FutureSort
}
