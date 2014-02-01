package ddaq.sensors

import scala.math._
import scala.collection.SortedMap

sealed trait SensorOutput
trait Volts extends SensorOutput
trait Frequency extends SensorOutput

trait Sensor {
	def unapply(out: Double): Double
}
trait InvertedSensor {
	def apply(in: Double): Double
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
	def linear(offset: Double, coef: Double) = Sensor.invertible(i => (i - offset) / coef, o => offset + coef * o)

	// def lookup(table: Map[Double,Double]) = new InvertibleSensor {}

	/*def interpolateLinear(table: Array[(Double,Double)], in: Double) =
		if(table.length < 2)
			None
			else {
				var i = table.length / 2
				var last = -1
				while(i != last) {
					val (k,v) = table(i)
					if(k == in)
						return Some(v)

					var last = i
					val step = (table.length - i) / 2				
					i = i + (if((in - k) > 0) step else -step)
				}
				val (k1,v1) = table(i)
				ai = if((in - k1) > 0) i+1 else i-1
				if(ai < 0 || ai >= table.length)
					None
					else {
						val (k2,v2) = table(ai)
						val between = abs(k2 - k1) /
					}
			}*/
}

trait SensorSubstitution {
	def defaultSensor: InvertedSensor
	def newSensor: Sensor

	def apply(badIn: Double) = newSensor.unapply(defaultSensor(badIn))
}