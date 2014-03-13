package ddaq

import ddaq.sensors._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

trait SourceProtocol
object TestProtocol extends SourceProtocol

case class Source(name: String, sensors: Map[Sensor,Channel.Channel[_]])