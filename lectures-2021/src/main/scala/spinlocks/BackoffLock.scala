package spinlocks

import java.util.concurrent.atomic.AtomicBoolean

/**
  * @author Ilya Sergey
  */
class BackoffLock extends SpinLock {
  val state = new AtomicBoolean(false)
  private val MIN_DELAY = 1
  private val MAX_DELAY = 2048

  override def lock(): Unit = {
    val backoff = new Backoff(MIN_DELAY, MAX_DELAY)
    while (true) {
      while (state.get()) {}
      if (!state.getAndSet(true)) {
        return
      } else {
        backoff.backoff()
      }
    }
  }

  override def unlock() = {
    state.set(false)
  }

}
