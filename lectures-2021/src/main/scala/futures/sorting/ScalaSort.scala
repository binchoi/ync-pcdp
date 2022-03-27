package futures.sorting

/**
  * @author Ilya Sergey
  */
object ScalaSort extends Sorting {
  override def sort[T: Ordering](a: Array[T]): Unit = scala.util.Sorting.quickSort(a)
  override def getName = "ScalaSort "
}