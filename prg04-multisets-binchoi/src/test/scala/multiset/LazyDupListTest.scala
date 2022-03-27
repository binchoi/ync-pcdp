package multiset

/**
  * @author Ilya Sergey
  */
class LazyDupListTest extends MultiSetTests with HashCollistionTests {
  override def mkSet = new LazyDupList[Int]

  override def mkCollisionFreeSet = new LazyDupList[MyObject]
}
