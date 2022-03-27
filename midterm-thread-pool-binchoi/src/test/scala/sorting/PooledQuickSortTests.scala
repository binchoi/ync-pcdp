package sorting

/**
  * @author Ilya Sergey
  */
class PooledQuickSortTests extends SortingTests {

  override val ARRAY_SIZE: Int = 5000

  val sorter = PooledQuickSort
}
