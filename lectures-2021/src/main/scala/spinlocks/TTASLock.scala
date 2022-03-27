package spinlocks

import java.util.concurrent.atomic.AtomicBoolean

/**
  * @author Ilya Sergey
  */
class TTASLock extends SpinLock {

  val state = new AtomicBoolean(false)

  override def lock(): Unit = {
    while (true) {
      while (state.get()) {
        // spin
      }
      if (!state.getAndSet(true)) {
        return 
      }
    }
  }

  override def unlock() = {
    state.set(false)
  }

}