package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import io.github.karols.units._

sealed trait SensorInput

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait SensorSpec[I <: MUnit,O <: MUnit] {
  val name: String
  val inputUpperRange: Option[DoubleU[I]] = None
  val inputLowerRange: Option[DoubleU[I]] = None
  val outputUpperRange: Option[DoubleU[O]] = None
  val outputLowerRange: Option[DoubleU[O]] = None
}

trait Sensor[I <: MUnit,O <: MUnit] extends SensorSpec[I,O] {
  val apply: DoubleU[I] => Option[DoubleU[O]]
}
trait InvertedSensor[I <: MUnit,O <: MUnit] extends SensorSpec[I,O] {
  val unapply: DoubleU[O] => Option[DoubleU[I]]
}
trait InvertibleSensor[I <: MUnit,O <: MUnit] extends InvertedSensor[I,O] with Sensor[I,O]

object Sensor {
	def apply[I <: MUnit,O <: MUnit](n: String, f: DoubleU[I] => Option[DoubleU[O]]) = new Sensor[I,O] {
    val name = n
    val apply = f
	}
	def inverted[I <: MUnit,O <: MUnit](n: String, f: DoubleU[O] => Option[DoubleU[I]]) = new InvertedSensor[I,O] {
    val name = n
		val unapply = f
	}

  def invertible[I <: MUnit,O <: MUnit](n: String, io: DoubleU[I] => Option[DoubleU[O]], oi: DoubleU[O] => Option[DoubleU[I]]) =
    new InvertibleSensor[I,O] {
      val name = n
      val apply = io
      val unapply = oi
   }

  def linear[I <: MUnit,O <: MUnit](n: String, offset: DoubleU[O], coef: DoubleU[O / I]) =
    invertible[I,O](n, i => offset + (coef.value * i.value).of[O], o => ((o - offset).value / coef.value).of[I])

  def lookup[I <: MUnit,O <: MUnit](n: String, map: Map[DoubleU[I],DoubleU[O]]) = new InvertibleSensor[I,O] {
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

trait SensorSubstitution[I <: MUnit,O <: MUnit] {
	def oldSensor: Sensor[I,O]
	def newSensor: InvertedSensor[I,O]

	val apply = (badIn: DoubleU[I]) => oldSensor.apply(badIn).flatMap(newSensor.unapply(_))
}