package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

package object implicits {
	implicit def toSome[A](a: A) = Some(a)

  // implicit def toChannel[A](s: Channel.Stream[A]) = Channel(s)

  implicit def combinedSample[A](value: Sample.Combined[A]) = Sample(value, value.map(_._2.timestamp).maxBy(_.getMillis))
}