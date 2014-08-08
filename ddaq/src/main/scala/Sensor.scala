package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import squants._

sealed trait SensorInput

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait Sensor {
  val name: String
  val inputUpperRange: Option[Quantity[_]] = None
  val inputLowerRange: Option[Quantity[_]] = None
  val outputUpperRange: Option[Quantity[_]] = None
  val outputLowerRange: Option[Quantity[_]] = None
}

trait IOSensor[I,O] extends Sensor {
  def unapply(out: Quantity[O]): Option[Quantity[I]]
}
trait OISensor[I,O] extends Sensor {
  def apply(in: Quantity[I]): Option[Quantity[O]]
}
trait InvertibleSensor[I,O] extends OISensor[I,O] with IOSensor[I,O]

object Sensor {
	def apply[I,O](n: String, f: Quantity[O] => Option[Quantity[I]]) = new IOSensor[I,O] {
    val name = n
		def unapply(out: Quantity[O]) = f(out)
	}
	def inverted[I,O](n: String, f: Quantity[I] => Option[Quantity[O]]) = new OISensor[I,O] {
    val name = n
		def apply(in: Quantity[I]) = f(in)
	}
	def invertible[I,O](n: String, io: Quantity[I] => Option[Quantity[O]], oi: Quantity[O] => Option[Quantity[I]]) = new InvertibleSensor[I,O] {
    val name = n
		def apply(in: Quantity[I]) = io(in)
		def unapply(out: Quantity[O]) = oi(out)
	}

  // out = offset + in * coef
  // in = (out - offset) / coef
	def linear[O <: Quantity[O]](n: String, offset: Quantity[O], coef: Double) =
    Sensor.invertible(n, i => offset + coef * i, o => (o - offset) / coef)

	def lookup[I,O](n: String, table: Map[Quantity[I],Quantity[O]]) = new InvertibleSensor[I,O] {
    val name = n
    private lazy val array = table.map(kv => (kv._1, kv._2)).toArray.sortBy(_._1)
    private lazy val inverseArray = array.map(kv => (kv._2, kv._1)).sortBy(_._1)

    def apply(in: Quantity[I]) = LookupTable.interpolateLinear(inverseArray, in.value)
    def unapply(in: Quantity[O]) = LookupTable.interpolateLinear(array, in.value)

    override val inputUpperRange = Some(table.values.maxBy(_.value))
    override val inputLowerRange = Some(table.values.minBy(_.value))
    override val outputUpperRange = Some(table.keys.maxBy(_.value))
    override val outputLowerRange = Some(table.keys.minBy(_.value))
  }
}

trait SensorCorrector[A <: Quantity[A]] extends Function1[A,Option[A]] {
  val name: String
}

/*trait SensorSubstitution[I <: Quantity[I],O <: Quantity[O]] extends SensorCorrector[O] {
	def defaultSensor: OISensor[I,O]
	def newSensor: IOSensor[I,O]

	// def apply(badIn: O) = defaultSensor.unapply(badIn).flatMap(newSensor.apply(_))
  def apply(badOut: O) = newSensor.apply(badIn).flatMap(defaultSensor.unapply(_))
}*/