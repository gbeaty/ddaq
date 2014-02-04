package ddaq.sensors

import ddaq.implicits._

import org.specs2.mutable._

class SensorsTests extends Specification {	

	"Lookup tables" should {
    val table = Map(0.0->0.0, 1.0->0.5, 2.0->1.0, 3.0->1.5, 4.0->2.0)
    val lookupSensor = Sensor.lookup(table)

    "Fail on out-of-bounds searches" in {
      lookupSensor.unapply(-1) === None
      lookupSensor.unapply(4.1) === None
    }
    "Lookup direct matches" in {
      table.map { kv => lookupSensor.unapply(kv._1) } ==== table.map { kv => Some(kv._2) }
      table.map { kv => lookupSensor.apply(kv._2) } ==== table.map { kv => Some(kv._1) }
    }
    "Interpolate" in {
      lookupSensor.unapply(0.5) ==== 0.25
      lookupSensor.unapply(3.5) ==== 1.75
      lookupSensor.unapply(2.1) ==== 1.05
    }
    "Interpolate inverse" in {
      lookupSensor.apply(0.25) ==== 0.5
      lookupSensor.apply(1.75) ==== 3.5
      lookupSensor.apply(1.05) ==== 2.1
    }
  }

  "Linear sensors" should {
    val sensor = Sensor.linear(1.0, 0.5)

    "Transfer" in {
      sensor.unapply(3.0) ==== 4.0
      sensor.apply(4.0) ==== 3.0
    }
  }
}