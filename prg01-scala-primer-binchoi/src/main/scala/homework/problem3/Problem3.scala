package homework.problem3

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
object Problem3 {
  
  def listToArray[T : ClassTag](l : List[T]): Array[T] = {
    val lengthOfList = l.length
    val res_array = new Array[T](lengthOfList)

    for (i <- 0 until lengthOfList) {
      res_array(i) = l(i)
    }
      res_array
  }
  
  def arrayToList[T](a: Array[T]): List[T] = {
    val lengthOfArray = a.length

    var res_list = List(): List[T]
    for (i <- (lengthOfArray-1) to 0 by -1) {
      res_list = a(i) :: res_list
    }
    res_list
  }

}
