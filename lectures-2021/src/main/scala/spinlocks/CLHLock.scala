package spinlocks

import java.util.concurrent.atomic.AtomicReference

/**
  * @author Ilya Sergey
  */
class CLHLock extends SpinLock {

  class QNode { // Queue node inner class
    @volatile
    var locked = false
  }

  val tail: AtomicReference[QNode] = new AtomicReference[QNode](new QNode)
  
  val myNode: ThreadLocal[QNode] = new ThreadLocal[QNode]() {
    override def initialValue(): QNode = {
      new QNode()
    }
  }

  val myPred: ThreadLocal[QNode] = new ThreadLocal[QNode]() {
    override def initialValue(): QNode = {
      null
    }
  }

  override def lock(): Unit = {
    val qnode = myNode.get() // use my node
    qnode.locked = true // announce start
    // Make me the new tail, and find my predecessor
    val pred = tail.getAndSet(qnode)
    myPred.set(pred) // remember predecessor
    while (pred.locked) {} // spin
  }

  override def unlock() = {
    val qnode = myNode.get() // use my node
    qnode.locked = false // announce finish
    myNode.set(myPred.get()) // reuse predecessor
  }
}
