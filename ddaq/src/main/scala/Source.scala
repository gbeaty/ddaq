package ddaq

import ddaq.sensors._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import scala.concurrent.duration._

import org.joda.time._

trait Source {
  val name: String
  val inputs: Map[String,Ddaq.Channel[_]]
}