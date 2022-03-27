package intro

/**
  * @author Ilya Sergey
  */

trait PrimePrinter {
  
  def primePrint(powerOfTen: Int): Unit

  class MyThread(powerOfTen: Int) extends Thread {
    override def run(): Unit = {
      primePrint(powerOfTen)
    }
  }
  

  def main(args: Array[String]): Unit = {
    val threads: Seq[MyThread] = (0 to 9).map(_ => new MyThread(6))

    val t0 = System.currentTimeMillis()
    for (t <- threads) {
      t.start()
    }
    for (t <- threads) {
      t.join()
    }
    val t1 = System.currentTimeMillis()
    println()
    println("Elapsed time: " + (t1 - t0) + " ms")

  }
  
  
}
