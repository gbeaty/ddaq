package ddaq.sensors

import ddaq.Ddaq._

import io.github.karols.units._
import SI._

import org.specs2.mutable._

class SensorsTests extends Specification {

	"Lookup tables" should {
    val table = Map(0.0-> -0.5, 1.0->0.5, 2.0->1.0, 3.0->1.5, 4.0->2.0, 5.0->4.0).map(kv => (kv._1.of[bar], kv._2.of[volt]))
    val ls = Sensor.lookup("", table)

    implicit def toUnit[U <: MUnit](d: Double) = d.of[U]

    "Fail on out-of-bounds searches" in {
      ls.unapply(-1.0) === None
      ls.unapply(4.1) === None
      ls.apply(-0.5) ==== None
      ls.apply(5.1) ==== None
    }
    "Lookup direct matches" in {
      table.map { kv => ls.apply(kv._1) } ==== table.map { kv => Some(kv._2) }
      table.map { kv => ls.unapply(kv._2) } ==== table.map { kv => Some(kv._1) }
    }
    "Interpolate" in {      
      ls.apply(0.5).get.value must beCloseTo(0.0, 0.0000001)
      ls.apply(3.5).get.value must beCloseTo(1.75, 0.0000001)
      ls.apply(2.1).get.value must beCloseTo(1.05, 0.0000001)
      ls.apply(4.8).get.value must beCloseTo(3.6, 0.0000001)
    }
    "Interpolate inverse" in {
      ls.unapply(0.0).get ==== 0.5
      ls.unapply(1.75).get ==== 3.5
      ls.unapply(1.05).get ==== 2.1
      ls.unapply(3.6).get ==== 4.8
    }
    "Compute ranges" in {
      ls.inputUpperRange ==== Some(5.0)
      ls.inputLowerRange ==== Some(0.0)
      ls.outputUpperRange ==== Some(4.0)
      ls.outputLowerRange ==== Some(-0.5)
    }
  }

  "Linear sensors" should {
    val sensor = Sensor.linear("", 1.0.of[volt], 0.5.of[volt / bar])

    "Transfer" in {
      sensor.unapply(3.0.of[volt]) ==== 4.0.of[bar]
      sensor.apply(4.0.of[bar]) ==== 3.0.of[volt]
    }
  }
}