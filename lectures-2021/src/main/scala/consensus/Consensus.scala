package consensus

/**
  * @author Ilya Sergey
  */
trait Consensus[T] {
  
  def decide(value: T): T

}
