package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import scalaz._
import Scalaz._

import scunits._
import Scunits._

class LookupTable[K <: Dims,V <: Dims](map: Map[Measure[K],Measure[V]]) {

  def arrayify[K <: Dims,V <: Dims](map: Map[Measure[K], Measure[V]]): (ArrayM[K],ArrayM[V]) = {
    val kvs = map.toSeq.sortBy(_._1)
    (ArrayM(kvs.map(_._1.v).toArray), ArrayM(kvs.map(_._2.v).toArray))
  }
  
  protected val (keys, values) = arrayify(map)
  protected val (inverseKeys, inverseValues) = arrayify(map.map(kv => (kv._2, kv._1)))

  val apply = LookupTable.interpolateLinear(keys, values) _
  val unapply = LookupTable.interpolateLinear(inverseKeys, inverseValues) _
  val inputUpperRange = map.keys.maxBy(_.v)
  val inputLowerRange = map.keys.minBy(_.v)
  val outputUpperRange = map.values.maxBy(_.v)
  val outputLowerRange = map.values.minBy(_.v)
}
object LookupTable {

  def interpolateLinear[K <: Dims,V <: Dims](keys: ArrayM[K], values: ArrayM[V])(in: Measure[K]): Option[Measure[V]] = {
    var lowerIndex = 0
    var upperIndex = keys.length - 1
    if(in < keys(0) || in > keys(upperIndex))
      None
      else {
        while((upperIndex - lowerIndex) > 1) {
          var i = lowerIndex + (upperIndex - lowerIndex) / 2
          var k = keys(i)

          if(k === in)
            return Some(values(i))

          if(in > k)
            lowerIndex = i
            else upperIndex = i
        }

        val k1 = keys(lowerIndex)
        val k2 = keys(upperIndex)
        val v1 = values(lowerIndex)
        val v2 = values(upperIndex)
        val percent = ((in - k1) / (k2 - k1))
        val diff = v2 - v1
        Some(v1 + diff.mult(percent.v))
      }
  }
}