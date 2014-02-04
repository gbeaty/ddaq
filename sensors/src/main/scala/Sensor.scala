package ddaq.sensors

import ddaq.implicits._

import scala.math._
import scala.collection.SortedMap

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait Sensor {
	def unapply(out: Double): Option[Double]
}
trait InvertedSensor {
	def apply(in: Double): Option[Double]
}
trait InvertibleSensor extends Sensor with InvertedSensor

object Sensor {
	def apply(f: Double => Double) = new Sensor {
		def unapply(out: Double) = f(out)
	}
	def inverted(f: Double => Double) = new InvertedSensor {
		def apply(in: Double) = f(in)
	}
	def invertible(io: Double => Double, oi: Double => Double) = new InvertibleSensor {
		def apply(in: Double) = io(in)
		def unapply(out: Double) = oi(out)
	}

  // out = offset + in * coef
  // in = (out - offset) / coef
	def linear(offset: Double, coef: Double) = Sensor.invertible(i => offset + coef * i, o => (o - offset) / coef)

	def lookup(table: Map[Double,Double]) = new InvertibleSensor {
    private lazy val array = table.toArray.sortBy(_._1)
    private lazy val inverseArray = array.map(kv => (kv._2,kv._1)).sortBy(_._1)

    def apply(in: Double) = interpolateLinear(inverseArray, in)
    def unapply(in: Double) = interpolateLinear(array, in)
  }

	def interpolateLinear(table: Array[(Double,Double)], in: Double): Option[Double] =
		if(table.length < 2 || in < table(0)._1 || in > table(table.length - 1)._1)
			None
			else {
				var i = table.length / 2
        var step = i
        var k, v = 0.0
				while(step > 0) {
					k = table(i)._1
          v = table(i)._2
					if(k == in)
						return Some(v)

					step = step / 2
					i = i + (if((in - k) > 0) step else -step)
				}
				if(k > in) {
          i -= 1
          k = table(i)._1
          v = table(i)._2
        }
        val (k2,v2) = table(i+1)
        val percent = (in - k) / (k2 - k)
        val diff = v2 - v
        Some(v + diff * percent)
			}
}

trait SensorCorrector extends Function1[Double,Option[Double]]

trait SensorSubstitution extends SensorCorrector {
	def defaultSensor: InvertedSensor
	def newSensor: Sensor

	def apply(badIn: Double) = defaultSensor(badIn).flatMap(newSensor.unapply(_))
}