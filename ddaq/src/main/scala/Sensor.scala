package ddaq.sensors

import ddaq.implicits._

import scala.math._
import scala.collection.SortedMap

sealed trait SensorInput

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait Sensor {
  val name: String
  val inputUpperRange: Option[Double] = None
  val inputLowerRange: Option[Double] = None
  val outputUpperRange: Option[Double] = None
  val outputLowerRange: Option[Double] = None
}

trait DefinedSensor extends Sensor {
  def unapply(out: Double): Option[Double]
}
trait InvertedSensor extends Sensor {
  def apply(in: Double): Option[Double]
}
trait InvertibleSensor extends InvertedSensor with DefinedSensor

object Sensor {
	def apply(n: String, f: Double => Option[Double]) = new DefinedSensor {
    val name = n
		def unapply(out: Double) = f(out)
	}
	def inverted(n: String, f: Double => Option[Double]) = new InvertedSensor {
    val name = n
		def apply(in: Double) = f(in)
	}
	def invertible(n: String, io: Double => Option[Double], oi: Double => Option[Double]) = new InvertibleSensor {
    val name = n
		def apply(in: Double) = io(in)
		def unapply(out: Double) = oi(out)
	}

  // out = offset + in * coef
  // in = (out - offset) / coef
	def linear(n: String, offset: Double, coef: Double) = Sensor.invertible(n, i => offset + coef * i, o => (o - offset) / coef)

	def lookup(n: String, table: Map[Double,Double]) = new InvertibleSensor {
    val name = n
    private lazy val array = table.toArray.sortBy(_._1)
    private lazy val inverseArray = array.map(kv => (kv._2,kv._1)).sortBy(_._1)

    def apply(in: Double) = LookupTable.interpolateLinear(inverseArray, in)
    def unapply(in: Double) = LookupTable.interpolateLinear(array, in)

    override val inputUpperRange = Some(table.values.max)
    override val inputLowerRange = Some(table.values.min)
    override val outputUpperRange = Some(table.keys.max)
    override val outputLowerRange = Some(table.keys.min)
  }
}

trait SensorCorrector extends Function1[Double,Option[Double]] {
  val name: String
}

trait SensorSubstitution extends SensorCorrector {
	def defaultSensor: InvertedSensor
	def newSensor: DefinedSensor

	def apply(badIn: Double) = defaultSensor.apply(badIn).flatMap(newSensor.unapply(_))
}