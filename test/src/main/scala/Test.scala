package ddaq.test

import ddaq._
import Ddaq._

import scala.concurrent.duration._
import scalaz.stream._
import org.joda.time._

object SystemOut extends Display {
  val name = "System.out"

  def apply[A](chanName: String, sample: Option[Sample[A]]) {
    print(chanName + ": " + sample.map(_.value).getOrElse("----"))
  }
}

object TestSource extends Source {
 val name = "test source"

 def randomVolts = Process.awakeEvery(1 seconds).map { _ =>
  val rand = new scala.util.Random
  Sample(rand.nextDouble * 5.0, new DateTime)
 }

 // val rpm = 

 val inputs = Map("adc1" -> randomVolts, "adc2" -> randomVolts)
}