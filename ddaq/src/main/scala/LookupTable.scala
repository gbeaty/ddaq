package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import io.github.karols.units._
import io.github.karols.units.arrays._
import DoubleU._

class LookupTable[K <: MUnit,V <: MUnit](map: Map[DoubleU[K],DoubleU[V]]) {

  def arrayify[K <: MUnit,V <: MUnit](map: Map[DoubleU[K], DoubleU[V]]) = {
    val kvs = map.toSeq.sortBy(_._1)
    (DoubleUArray(kvs.map(_._1): _*), DoubleUArray(kvs.map(_._2): _*))
  }
  
  protected val (keys, values) = arrayify(map)
  protected val (inverseKeys, inverseValues) = arrayify(map.map(kv => (kv._2, kv._1)))

  val apply = LookupTable.interpolateLinear(keys, values) _
  val unapply = LookupTable.interpolateLinear(inverseKeys, inverseValues) _
  val inputUpperRange = map.keys.maxBy(_.value)
  val inputLowerRange = map.keys.minBy(_.value)
  val outputUpperRange = map.values.maxBy(_.value)
  val outputLowerRange = map.values.minBy(_.value)
}
object LookupTable {

  def interpolateLinear[K <: MUnit,V <: MUnit](keys: DoubleUArray[K], values: DoubleUArray[V])(in: DoubleU[K]): Option[DoubleU[V]] = {
    var lowerIndex = 0
    var upperIndex = keys.length - 1
    if(in < keys(0) || in > keys(upperIndex))
      None
      else {
        while((upperIndex - lowerIndex) > 1) {          
          var i = lowerIndex + (upperIndex - lowerIndex) / 2
          var k = keys(i)

          if(k == in.value)
            return Some(values(i))

          if(in > k)
            lowerIndex = i
            else upperIndex = i
        }

        val k1 = keys(lowerIndex)
        val k2 = keys(upperIndex)
        val v1 = values(lowerIndex)
        val v2 = values(upperIndex)
        val percent = ((in - k1) / (k2 - k1)).value
        val diff = v2 - v1
        Some(v1 + diff.times(percent))
      }
  }

  /*def interpolateLinear[K <: MUnit,V <: MUnit](table: Array[(DoubleU[K],DoubleU[V])])(in: DoubleU[K]): Option[DoubleU[V]] =
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
      }*/
}