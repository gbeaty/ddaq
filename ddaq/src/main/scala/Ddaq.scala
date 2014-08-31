package ddaq

import org.joda.time._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

trait Platform

object Ddaq {
  type Input[A] = Process[Task,Sample[A]]

  implicit def toSome[A](a: A): Option[A] = Some(a)

  // implicit def toChannel[A](s: Channel.Stream[A]) = Channel(s)

  implicit def combinedSample[A](value: Sample.Combined[A]) = Sample(value, value.map(_._2.timestamp).maxBy(_.getMillis))
}

case class Ddaq(ins: Set[Channel[_]], dash: Dash, presenter: Presenter) {
  // val in = ins.
}