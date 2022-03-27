package pool

import util.ThreadID

import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.collection.mutable

/**
  * A simplistic thread pool implementation
  */
class ThreadPool(private val n: Int) {

  // A queue of pending tasks to be implemented
  // TODO: Notice that this is a simple sequential (non-concurrent) queue.
  //       Can you explain why it is made this way?  --- (to follow FIFO) - synchronized
  //       queue can be ... but synchronized enq (no need to use more processing power / space)
  private val taskQueue = new mutable.Queue[Unit => Unit]()

  // An array of worker threads that are allocated to execute tasks in parallel.
  ThreadID.reset()
  private val workers: Array[Worker] = {
    // TODO: Populate the array by the Worker thread instances
    //       with the id's corresponding to their positions in the array
    (0 until n).toArray.map(new Worker(_))
  }

  // An array of flags indicating which threads are currently active
  // TODO: Notice that this is a sequential, non-atomic array. This is intentional.
  //       Can you explain why? --- (multiple threads should be able to update their status
  //       simultaneously and they work in separate disjoint field so they can't affect each other anyways) - for actions that need
  private val workInProgress = Array.fill(n)(false)

  // A flag indicating that the thread-pool is no longer usable
  // It is annotated as @volatile. Make sure to use it as such.
  @volatile
  private var isShutDown = false

  // TODO: feel free to add other fields if you need them
  val taskQueueLock = new ReentrantLock
  val noTask: Condition = taskQueueLock.newCondition()
  val stillTasksOrWorkers: Condition = taskQueueLock.newCondition()

  {
    // TODO: Don't forget to start all the pooled threads when creating the object
    for (w <- workers) {
      w.start()
    }
  }

  private class Worker(val id: Int) extends Thread {
    override def run() = {
      // Next task
      var task: Unit => Unit = null
      try { // interruptError can happen anywhere - so wrap around all
        while (!isShutDown) {  /* RODO: What's the condition here? */
          // TODO: [prelude] try to fetch the new task from the task queue
          //       This part requires mutually exclusive access to the ThreadPool state to check
          //       the status of the queue
          taskQueueLock.lock()
          try {
            while (taskQueue.isEmpty) {
              noTask.await()
            }
            task = taskQueue.dequeue()
            workInProgress(id) = true
          } finally {
            taskQueueLock.unlock()
          }

          task()
          // TODO: [epilogue] Mark the thread as awaiting the next task
          //       Beware of deadlocks (or, in this case, wait-locks :-).
          workInProgress(id) = false
          task = null

          taskQueueLock.lock()
          try {
            if (taskQueue.isEmpty) {
              stillTasksOrWorkers.signalAll()
            }
          } finally {
            taskQueueLock.unlock()
          }
        }
      } catch {
        case e: InterruptedException =>
          // The thread has received an interrupt call
//          println(s"Thread $id is now stopped.")
          workInProgress(id) = false
      }

    }
  }

  def workOngoing() : Boolean = {
    workInProgress.reduceLeft(_ || _)
  }

  /**
    * Shuts down the thread pool by interrupting all its worker threads.
    * After this the thread pool is no longer usable.
    */
  def shutdown(): Unit = {
    if (isShutDown) throw ThreadPoolException("Thread pool is no longer active") // no need to interrupt
    for (i <- workers.indices) {
      workers(i).interrupt()
    }
    // wait until all threads have successfully been interrupted -- is this needed...
    while (workOngoing()) {} // maybe not neccessary
    // only finish execution of shutdown after all work is finished
    isShutDown = true
  }


  /**
    * Schedule a new task for execution by some thread in the thread pool.
    * `async()` doe not block the caller, but neither does it guarantee that 
    * the task will be completed before `async()` returns.
    *
    * @param task a task to execute concurrently
    */
  def async(task: Unit => Unit): Unit = {
    // TODO: Implement me!
    //       What's going to happen if the pool is shut down already?
    //       How do we handle the task queue?
    //       How does this method interact with worker threads, letting them know
    //       that a new task has been enqueued?
      if (isShutDown) throw ThreadPoolException("Thread pool is no longer active") // or just return None
      taskQueueLock.lock()
      try {
        taskQueue.enqueue(task)
        if (taskQueue.size == 1) {
          noTask.signalAll() // contention is ok?
        }
//        if (taskQueue.size > 1) {
//          noTask.signalAll()
//        }
      } finally {
        taskQueueLock.unlock()
      }
  }


  /**
    * Takes an initial tasks and blocks until all threads in the pool finish their work.
    * That is, the other threads may allocate future tasks. Yet, the thread that
    * invoked this method will be blocked until the process of creating tasks
    * and completing them reaches a "ele". Make sure to understand how
    * the "quiescence" for the thread pool is defined.
    *
    * @param task an initial task to executed by some thread in the pool
    */
  def startAndWait(task: Unit => Unit): Unit = {
    // TODO: Implement according to the specification.
    //       How would we know when to unblock?   
    //       Handle if is in `isShutDown` mode.
    if (isShutDown) throw ThreadPoolException("Thread pool is no longer active") // or just {return}

    this.async(task)

    taskQueueLock.lock()
    try {
      while (taskQueue.nonEmpty || workOngoing()) {
          stillTasksOrWorkers.await()
      }
    } finally {
      taskQueueLock.unlock()
    }

    // SCRATCH
//    var workOngoing: Boolean = false
//    do {
//      taskQueueLock.lock()
//      workInProgressLock.lock()
//      try {
//      workOngoing = false
//      for (w <- workInProgress) {
//        workOngoing = workOngoing || w
//      }
//      } finally {
//        taskQueueLock.unlock()
//        workInProgressLock.unlock()
//      }
//    } while (workOngoing || taskQueue.nonEmpty) // should the taskQueue really be empty for startandwait to execute the task
//    this.async(task) // - do I assign it to a worker thread?
//    task() // or should the caller method execute the task
//    alternatively, i can directly assign to worker x (like worker 0)... is there a specification
  }

}

/**
  * An exception used to indicate the invalid state of this thread pool 
  */
case class ThreadPoolException(msg: String) extends Exception(msg)
