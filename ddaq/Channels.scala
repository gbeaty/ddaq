package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.stream.Process._

import org.joda.time._

case class Sample[A](value: A, timestamp: DateTime) {
  def map[B](f: A => B) = Sample(f(value), timestamp)
}
object Sample {
  implicit def orderer[A] = Order.order[Sample[A]] { (s1,s2) =>
    if(s1.timestamp.getMillis > s2.timestamp.getMillis)
      Ordering.GT
    else if(s2.timestamp.getMillis < s2.timestamp.getMillis)
      Ordering.LT
    else Ordering.EQ
  }
}

case class Channel[A](name: String, stream: Process[Task,Sample[A]])