package ddaq.channel

import ddaq._

package object vehicle {
  private def ct(ns: String*) = new ChannelType(ns: _*)

  val speed = ct("speed", "spd", "SP")
  val acceleration = ct("acceleration", "accel", "acc")
  val longitudinalAccel = ct("longitudinal acceleration", "longitudinal accel", "long accel", "long")
  val lateralAccel = ct("lateral acceleration", "lateral accel", "lat accel", "lat")
  val verticleAccel = ct("vertical acceleration", "vertical accel", "vert accel", "vert")
}