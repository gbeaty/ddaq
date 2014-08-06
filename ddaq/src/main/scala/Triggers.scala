package ddaq

import Ddaq._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

case class Trigger(stream: Ddaq.Channel[Boolean], channels: Set[Ddaq.Channel[_]]) {
  def op(op: (Boolean, Boolean) => Boolean)(t2: Trigger*) = Trigger(
    channels = channels ++ t2.map(_.channels).flatten,
    stream = Channel.lasts(t2.map(_.stream): _*).map { m =>
      val vs = m.values
      Sample(
        vs.tail.foldLeft(vs.head.value) { (res,el) => op(res, el.value) },
        m.timestamp
      )
    }
  )

  val and = op((a,b) => a && b) _
  val or = op((a,b) => a || b) _
  val nand = op((a,b) => !(a && b)) _
  val xor = op((a,b) => a != b) _
}