package stacks

import scala.concurrent.TimeoutException
import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class EliminationBackoffStack[T: ClassTag] extends LockFreeStack[T] {

  val eliminationArray = new EliminationArray[T]
  val policy = new ThreadLocal[RangePolicy]() {
    override protected def initialValue = new RangePolicy(eliminationArray.getSize)
  }

  override def push(value: T): Unit = {
    val rangePolicy = policy.get()
    val node = new Node(value)
    while (true) {
      if (tryPush(node)) {
        return
      } else try {
        val otherValue = eliminationArray.visit(value, rangePolicy.getRange)
        if (otherValue == null) {
          rangePolicy.recordEliminationSuccess()
          // Exchanged with concurrent pop
          return
        }
      } catch {
        case _: TimeoutException =>
          rangePolicy.recordEliminationTimeout()
      }
    }
  }

  override def pop(): T = {
    val rangePolicy = policy.get()
    while (true) {
      val returnNode = tryPop()
      if (returnNode != null) {
        return returnNode.value
      } else try {
        val otherValue = eliminationArray.visit(null.asInstanceOf[T], rangePolicy.getRange)
        if (otherValue != null) {
          rangePolicy.recordEliminationSuccess()
          return otherValue
        }
      } catch {
        case _: TimeoutException =>
          rangePolicy.recordEliminationTimeout()
      }
    }
    throw new Exception("[EliminationBackoffStack] Cannot happen")
  }

  override def toListThreadUnsafe = super.toListThreadUnsafe
}

/**
  * A class determining the range to to perform elimination 
  */
class RangePolicy(val maxRange: Int) {
  var currentRange = 1

  def recordEliminationSuccess(): Unit = {
    if (currentRange < maxRange) {
      currentRange += 1
      currentRange - 1
    }
  }

  def recordEliminationTimeout(): Unit = {
    if (currentRange > 1) {
      currentRange -= 1
      currentRange + 1
    }
  }

  def getRange: Int = currentRange
}