package ddaq.sensors

import scala.reflect.runtime.universe._

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import scunits._
import Scunits._

sealed trait SensorInput

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait SensorSpec[I <: Dims,O <: Dims] {
  val name: String
  val inTag: TypeTag[I]
  val outTag: TypeTag[O]
  val inputUpperRange: Option[Measure[I]] = None
  val inputLowerRange: Option[Measure[I]] = None
  val outputUpperRange: Option[Measure[O]] = None
  val outputLowerRange: Option[Measure[O]] = None
}

trait Sensor[I <: Dims,O <: Dims] extends SensorSpec[I,O] {
  val apply: Measure[I] => Option[Measure[O]]
}
trait InvertedSensor[I <: Dims,O <: Dims] extends SensorSpec[I,O] {
  val unapply: Measure[O] => Option[Measure[I]]
}
trait InvertibleSensor[I <: Dims,O <: Dims] extends InvertedSensor[I,O] with Sensor[I,O]

object Sensor {
	def apply[I <: Dims,O <: Dims](n: String, f: Measure[I] => Option[Measure[O]])(implicit it: TypeTag[I], ot: TypeTag[O]) = 
    new Sensor[I,O] {
      val inTag = it
      val outTag = ot
      val name = n
      val apply = f
	 }
	def inverted[I <: Dims,O <: Dims](n: String, f: Measure[O] => Option[Measure[I]])(implicit it: TypeTag[I], ot: TypeTag[O]) =
    new InvertedSensor[I,O] {
      val inTag = it
      val outTag = ot
      val name = n
		  val unapply = f
	 }

  def invertible[I <: Dims,O <: Dims](n: String, io: Measure[I] => Option[Measure[O]], oi: Measure[O] => Option[Measure[I]])
    (implicit it: TypeTag[I], ot: TypeTag[O]) =
      new InvertibleSensor[I,O] {
        val inTag = it
        val outTag = ot
        val name = n
        val apply = io
        val unapply = oi
      }

  def linear[I <: Dims,O <: Dims](n: String, offset: Measure[O], coef: Measure[O#Div[I]])(implicit it: TypeTag[I], ot: TypeTag[O]) =
    invertible[I,O](n, i => Some(offset + i * coef), o => Some((o - offset) / coef))

  def lookup[I <: Dims,O <: Dims](n: String, map: Map[Measure[I],Measure[O]])(implicit it: TypeTag[I], ot: TypeTag[O]) =
    new InvertibleSensor[I,O] {
      val inTag = it
      val outTag = ot
      val name = n
      val table = new LookupTable(map)

      val apply = table.apply
      val unapply = table.unapply

      override val inputUpperRange = Some(table.inputUpperRange)
      override val inputLowerRange = Some(table.inputLowerRange)
      override val outputUpperRange = Some(table.outputUpperRange)
      override val outputLowerRange = Some(table.outputLowerRange)
    }
}

trait SensorSubstitution[I <: Dims,O <: Dims] {
	def oldSensor: Sensor[I,O]
	def newSensor: InvertedSensor[I,O]

	val apply = (badIn: Measure[I]) => oldSensor.apply(badIn).flatMap(newSensor.unapply(_))
}