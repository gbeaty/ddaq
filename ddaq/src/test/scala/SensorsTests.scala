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
  sequential
  val err = 0.0000001

	"Lookup tables" should {
    val table = Map(0.0-> -0.5, 1.0->0.5, 2.0->1.0, 3.0->1.5, 4.0->2.0, 5.0->4.0).map(kv => (bar(kv._1), volt(kv._2)))
    val ls = Sensor.lookup("", table)

    "Fail on out-of-bounds searches" in {
      ls.unapply(volt(-1.0)) === None
      ls.unapply(volt(4.1)) === None
      ls.apply(bar(-0.5)) ==== None
      ls.apply(bar(5.1)) ==== None
    }
    "Lookup direct matches" in {
      table.map { kv => ls.apply(kv._1) } ==== table.map { kv => Some(kv._2) }
      table.map { kv => ls.unapply(kv._2) } ==== table.map { kv => Some(kv._1) }
    }
    "Interpolate" in {      
      ls.apply(bar(0.5)).get.v must beCloseTo(0.0, err)
      ls.apply(bar(3.5)).get.v must beCloseTo(1.75, err)
      ls.apply(bar(2.1)).get.v must beCloseTo(1.05, err)
      ls.apply(bar(4.8)).get.v must beCloseTo(3.6, err)
    }
    "Interpolate inverse" in {
      ls.unapply(volt(0.0)).get ==== bar(0.5)
      ls.unapply(volt(1.75)).get ==== bar(3.5)
      ls.unapply(volt(1.05)).get ==== bar(2.1)
      ls.unapply(volt(3.6)).get ==== bar(4.8)
    }
    "Compute ranges" in {
      ls.inputUpperRange ==== Some(bar(5.0))
      ls.inputLowerRange ==== Some(bar(0.0))
      ls.outputUpperRange ==== Some(volt(4.0))
      ls.outputLowerRange ==== Some(volt(-0.5))
    }
  }

  "Linear sensors" should {
    val sensor = Sensor.linear[Pressure,Electric.Potential]("", volt(1.0), (volt / bar)(0.5))

    "Transfer" in {
      sensor.unapply(volt(3.0)).get.v must beCloseTo(bar(4.0).v, err)
      sensor.apply(bar(4.0)) ==== volt(3.0)
    }
  }
}