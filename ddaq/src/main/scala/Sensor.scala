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
  def unapply(out: DoubleU[I]): Option[DoubleU[O]]
}
trait InvertedSensor[I <: MUnit,O <: MUnit] extends SensorSpec[I,O] {
  def apply(in: DoubleU[O]): Option[DoubleU[I]]
}
trait InvertibleSensor[I <: MUnit,O <: MUnit] extends InvertedSensor[I,O] with Sensor[I,O]

object Sensor {
	def apply[I <: MUnit,O <: MUnit](n: String, f: DoubleU[I] => Option[DoubleU[O]]) = new Sensor[I,O] {
    val name = n
		def unapply(in: DoubleU[I]) = f(in)
	}
	def inverted[I <: MUnit,O <: MUnit](n: String, f: DoubleU[O] => Option[DoubleU[I]]) = new InvertedSensor[I,O] {
    val name = n
		def apply(out: DoubleU[O]) = f(out)
	}

  object Invertible {
    def apply[I <: MUnit,O <: MUnit](n: String, io: DoubleU[I] => Option[DoubleU[O]], oi: DoubleU[O] => Option[DoubleU[I]]) =
      new InvertibleSensor[I,O] {
        val name = n
        def apply(in: DoubleU[I]) = io(in)
        def unapply(out: DoubleU[O]) = oi(out)
     }

    def linear[I <: MUnit,O <: MUnit](n: String, offset: DoubleU[O], coef: DoubleU[O / I]) =
      Sensor.Invertible[I,O](n, i => offset + (coef.value * i.value).of[O], o => ((o - offset).value / coef.value).of[I])

    def lookup[I <: MUnit,O <: MUnit](n: String, table: Map[DoubleU[I],DoubleU[O]]) = new InvertibleSensor[I,O] {
      val name = n
      private lazy val array = table.toArray.sortBy(_._1)
      private lazy val inverseArray = array.map(kv => (kv._2,kv._1)).sortBy(_._1)

      def apply(in: DoubleU[I]) = LookupTable.interpolateLinear(array, in)
      def unapply(out: DoubleU[O]) = LookupTable.interpolateLinear(inverseArray, out)

      override val inputUpperRange = Some(table.values.max)
      override val inputLowerRange = Some(table.values.min)
      override val outputUpperRange = Some(table.keys.max)
      override val outputLowerRange = Some(table.keys.min)
    }
  }
}

trait SensorCorrector[O] extends Function1[DoubleU[O],Option[DoubleU[O]]] {
  val name: String
}

trait SensorSubstitution[I <: MUnit,O <: MUnit] extends SensorCorrector[O] {
	def defaultSensor: InvertedSensor[I,O]
	def newSensor: Sensor[I,O]

	def apply(badIn: DoubleU[O]) = defaultSensor.apply(badIn).flatMap(newSensor.unapply(_))
}