package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

trait Trigger {
  val stream: Process[Task,Boolean]
  val channels: Set[ddaq.Channel[_]]

  def op(op: (Boolean, Boolean) => Boolean)(t2: Trigger) = {}

  val and = op((a,b) => a && b) _
  val or = op((a,b) => a || b) _
  val nand = op((a,b) => !(a && b)) _
  val xor = op((a,b) => a != b) _
}

object Trigger {
  def apply(s: Process[Task,Boolean], cs: Set[ddaq.Channel[_]]) = new Trigger {
    val stream = s
    val channels = cs
  }
}