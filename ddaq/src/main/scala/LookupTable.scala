package ddaq.sensors

import ddaq.implicits._

import scala.math._
import scala.collection.SortedMap

class LookupTable(map: Map[Double,Double]) {
  protected val table = map.toArray.sortBy(_._1)
  protected lazy val inverseTable = table.map(kv => (kv._2,kv._1)).sortBy(_._1)
  def apply(in: Double) = LookupTable.interpolateLinear(table, in)
  def unapply(out: Double) = LookupTable.interpolateLinear(inverseTable, out)
  val inputUpperRange = map.values.max
  val inputLowerRange = map.values.min
  val outputUpperRange = map.keys.max
  val outputLowerRange = map.keys.min
}

object LookupTable {

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