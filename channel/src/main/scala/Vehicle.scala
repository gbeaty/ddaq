package ddaq.channel

import ddaq._

import scunits._
import scunits.quantity._
import scunits.quantity.Automotive._

package object vehicle {
  val speed = new ChannelType[Speed]("speed", "spd", "SP")
  val acceleration = new ChannelType[Acceleration]("acceleration", "accel", "acc")
  val longitudinalAccel = new ChannelType[Acceleration]("longitudinal acceleration", "longitudinal accel", "long accel", "long")
  val lateralAccel = new ChannelType[Acceleration]("lateral acceleration", "lateral accel", "lat accel", "lat")
  val verticleAccel = new ChannelType[Acceleration]("vertical acceleration", "vertical accel", "vert accel", "vert")
  val fuelEconomy = new ChannelType[DistancePerFuel]("fuel economy", "fuel econ", "FE")
}