package ddaq.sensors

import ddaq.implicits._

import org.specs2.mutable._

class SensorsTests extends Specification {

	"Lookup tables" should {
    val table = Map(0.0->0.0, 1.0->0.5, 2.0->1.0, 3.0->1.5, 4.0->2.0)
    val ls = Sensor.lookup("", table)

    "Fail on out-of-bounds searches" in {
      ls.unapply(-1) === None
      ls.unapply(4.1) === None
    }
    "Lookup direct matches" in {
      table.map { kv => ls.unapply(kv._1) } ==== table.map { kv => Some(kv._2) }
      table.map { kv => ls.apply(kv._2) } ==== table.map { kv => Some(kv._1) }
    }
    "Interpolate" in {
      ls.unapply(0.5) ==== 0.25
      ls.unapply(3.5) ==== 1.75
      ls.unapply(2.1) ==== 1.05
    }
    "Interpolate inverse" in {
      ls.apply(0.25) ==== 0.5
      ls.apply(1.75) ==== 3.5
      ls.apply(1.05) ==== 2.1
    }
    "Compute ranges" in {
      ls.inputUpperRange ==== 2.0
      ls.inputLowerRange ==== 0.0
      ls.outputUpperRange ==== 4.0
      ls.outputLowerRange ==== 0.0
    }
  }

  "Linear sensors" should {
    val sensor = Sensor.linear("", 1.0, 0.5)

    "Transfer" in {
      sensor.unapply(3.0) ==== 4.0
      sensor.apply(4.0) ==== 3.0
    }
  }
}