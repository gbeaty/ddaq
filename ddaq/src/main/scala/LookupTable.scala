package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import io.github.karols.units._
import DoubleU._

// TODO: Use four DoubleUArrays instead of two arrays of (DoubleU,DoubleU).
class LookupTable[K <: MUnit,V <: MUnit](map: Map[DoubleU[K],DoubleU[V]]) {
  protected val table = map.toArray.sortBy(_._1.value)
  protected lazy val inverseTable = table.map(kv => (kv._2,kv._1)).sortBy(_._1.value)
  def apply(in: DoubleU[K]) = LookupTable.interpolateLinear(table, in)
  def unapply(out: DoubleU[V]) = LookupTable.interpolateLinear(inverseTable, out)
  val inputUpperRange = map.values.maxBy(_.value)
  val inputLowerRange = map.values.minBy(_.value)
  val outputUpperRange = map.keys.maxBy(_.value)
  val outputLowerRange = map.keys.minBy(_.value)
}

object LookupTable {

  def interpolateLinear[K <: MUnit,V <: MUnit](table: Array[(DoubleU[K],DoubleU[V])], in: DoubleU[K]): Option[DoubleU[V]] =
    if(table.length < 2 || in < table(0)._1 || in > table(table.length - 1)._1)
      None
      else {
        var i = table.length / 2
        var step = i
        var k = 0.0.of[K]
        var v = 0.0.of[V]
        while(step > 0) {
          k = table(i)._1
          v = table(i)._2
          if(k == in)
            return Some(v)

          step = step / 2
          i = i + (if((in - k) > 0.of[K]) step else -step)
        }
        if(k > in) {
          i -= 1
          k = table(i)._1
          v = table(i)._2
        }
        val (k2,v2) = table(i+1)
        val percent = (in - k).value / (k2 - k).value
        val diff = v2 - v
        Some(v + diff.times(percent))
      }
}