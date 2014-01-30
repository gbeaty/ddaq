package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

object Sample {
  
  type Source[A] = Process[Task,Sample[A]]
  protected[ddaq] type CS[A] = Map[Source[A],Sample[A]]

  def apply[A](v: A, ts: DateTime) = new Sample[A] {
    val value = v
    val timestamp = ts
  }

  implicit def toCombinedSample[A](in: CS[A]) = new CombinedSample[A] { val value = in }

  implicit def orderer[A] = Order.order[Sample[A]] { (s1,s2) =>
    if(s1.timestamp.getMillis > s2.timestamp.getMillis)
      Ordering.GT
    else if(s2.timestamp.getMillis < s2.timestamp.getMillis)
      Ordering.LT
    else Ordering.EQ
  }
}
import Sample._

trait Sample[A] {
  val value: A
  val timestamp: DateTime
  def map[B](f: A => B) = Sample(f(value), timestamp)
}  
trait CombinedSample[A] extends Sample[CS[A]] {
  val value: CS[A]
  val timestamp = value.map(_._2.timestamp).maxBy(_.getMillis)
}