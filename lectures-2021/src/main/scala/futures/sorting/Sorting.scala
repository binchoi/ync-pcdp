package futures.sorting

/**
  * An interface for generic in-place array sorting
  */
trait Sorting {

  /**
    * Sort an array of elements
    *
    * @tparam T a type of elements in the array. The subtype constraint `T : Ordering`
    *           indicates that `T` is expected to be a subtype of Comparable[T], i.e.,
    *           that one can use standard comparison operators on it for sorting 
    *
    * The return type of `sort` is `Unit`, meaning that the array is sorted in-place.
    */
  def sort[T : Ordering](a: Array[T]): Unit

  /**
    * Returns the name of the sorting method 
    */
  def getName: String

}
