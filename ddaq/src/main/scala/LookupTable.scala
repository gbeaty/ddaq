package ddaq.sensors

import ddaq._
import Ddaq._

import scala.math._
import scala.collection.SortedMap

import squants._

class LookupTable[K,V](map: Map[Quantity[K],Quantity[V]]) {
  protected val table = map.toArray.sortBy(_._1.value)
  protected val inverseTable = table.map(kv => (kv._2,kv._1)).sortBy(_._1.value)
  def apply(in: Quantity[K]) = LookupTable.interpolateLinear(table, in)
  def unapply(out: Quantity[V]) = LookupTable.interpolateLinear(inverseTable, out)
  val inputUpperRange = map.values.maxBy(_.value)
  val inputLowerRange = map.values.minBy(_.value)
  val outputUpperRange = map.keys.maxBy(_.value)
  val outputLowerRange = map.keys.minBy(_.value)
}

object LookupTable {

  def interpolateLinear[K,V](table: Array[(Quantity[K],Quantity[V])], in: Quantity[K]): Option[Quantity[V]] =
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