package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

package object trigger {

  case class Trigger(stream: Process[Task,Boolean], channels: Set[ddaq.Channel[_]]) {
    def op(op: (Boolean, Boolean) => Boolean)(t2: Trigger) = {
      /*val s2 = stream.merge(t2.stream).foldMap[Map[Trigger,Boolean]](t2.stream) { (ac,el) =>

      }*/
    }

    val and = op((a,b) => a && b) _
    val or = op((a,b) => a || b) _
    val nand = op((a,b) => !(a && b)) _
    val xor = op((a,b) => a != b) _
  }
}