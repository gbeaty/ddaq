package ddaq.sensors

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

	// def lookup(table: Map[Double,Double]) = 
}

trait SensorSubstitution {
	def defaultSensor: InvertedSensor
	def newSensor: Sensor

	def apply(badIn: Double) = newSensor.unapply(defaultSensor(badIn))
}