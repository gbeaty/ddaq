package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

object Sample {
  
  type Combined[A] = Map[Ddaq.Channel[A],Sample[A]]

  implicit def orderer[A] = Order.order[Sample[A]] { (s1,s2) =>
    if(s1.timestamp.getMillis > s2.timestamp.getMillis)
      Ordering.GT
    else if(s2.timestamp.getMillis < s2.timestamp.getMillis)
      Ordering.LT
    else Ordering.EQ
  }
}
import Sample._

case class Sample[A](value: A, timestamp: DateTime) {
  def map[B](f: A => B) = Sample(f(value), timestamp)
}

case class CombinedSample(value: Combined[_]) {
  val timestamp = value.values.map(_.timestamp).maxBy((ts: DateTime) => ts.getMillis)
}