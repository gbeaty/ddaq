package ddaq.sensors

import ddaq.Ddaq._

import scunits._
import scunits.quantity._
import scunits.unit.si._
import scunits.unit.us._
import scunits.unit.metric._
import scunits.unit.pressure._

import org.specs2.mutable._

class SensorsTests extends Specification {

	"Lookup tables" should {
    val table = Map(0.0-> -0.5, 1.0->0.5, 2.0->1.0, 3.0->1.5, 4.0->2.0, 5.0->4.0).map(kv => (bar(kv._1), volt(kv._2)))
    val ls = Sensor.lookup("", table)
    val err = 0.0000001

    implicit def toUnit[D <: Dims](d: Double) = Measure[D](d)

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
      ls.apply(bar(0.5)).get.v must beCloseTo(0.0, err)
      ls.apply(3.5).get.v must beCloseTo(1.75, err)
      ls.apply(2.1).get.v must beCloseTo(1.05, err)
      ls.apply(4.8).get.v must beCloseTo(3.6, err)
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
    val sensor = Sensor.linear[Pressure,Electric.Potential]("", volt(1.0), 0.5)

    "Transfer" in {
      sensor.unapply(volt(3.0)) ==== bar(4.0)
      sensor.apply(bar(4.0)) ==== volt(3.0)
    }
  }
}