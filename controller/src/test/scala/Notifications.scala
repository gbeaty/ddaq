package ddaq.displayController

import ddaq._

import org.specs2.mutable._
import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import scalaz.stream._

import org.joda.time._

object StartHere extends Specification {

  import Sample._

  def toSample(i: Int) = Sample(i, new DateTime(i))

  val seq = Seq(0,2,4,6)
  val one = Process.range(0,10,2).map(toSample(_))
  val two = Process.range(1,7,2).map(toSample(_))
  val merge = one merge two

  "Notifications" should {
    "Merge" in {
      merge.runLog.run.toSeq ==== scala.collection.immutable.Seq(0,1,2,3,4,5,6,8).map(toSample(_))
    }
    "Combine" in {
      Channel.lasts(one, two).runLog.run.toSeq ==== scala.collection.immutable.Seq(
        Map(                  ),
        Map(          two -> 1),
        Map(one -> 0, two -> 1),
        Map(one -> 0, two -> 3),
        Map(one -> 2, two -> 3),
        Map(one -> 2, two -> 5),
        Map(one -> 4, two -> 5),
        Map(one -> 6, two -> 5),
        Map(one -> 8, two -> 5)
      ).map(m => new CombinedSample(m.mapValues(toSample(_))))
    }
  }
}