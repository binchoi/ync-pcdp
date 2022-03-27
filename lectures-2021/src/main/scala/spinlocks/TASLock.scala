package spinlocks

import java.util.concurrent.atomic.AtomicBoolean

/**
  * @author Ilya Sergey
  */
class TASLock extends SpinLock {
  
  val state = new AtomicBoolean(false)
  
  override def lock() = {
    while(state.getAndSet(true)) {
      // spin
    }
  }

  override def unlock() = {
    state.set(false)
  }
  
}
