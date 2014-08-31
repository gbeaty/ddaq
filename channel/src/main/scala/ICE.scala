package ddaq.channel

import ddaq._
import Ddaq._

import scunits._
import scunits.quantity._
import scunits.quantity.Automotive._

package object ICE {
  val rpm = new ChannelType[AngularVelocity]("rpm")
  val oilPressure = new ChannelType[Pressure]("oil pressure", "oil pres", "oil P", "OP")
  val oilTemperature = new ChannelType[Temperature]("oil temperature", "oil temp", "oil T", "OT")
  val coolantTemperature = new ChannelType[Temperature]("water temperature", "water temp", "water T", "WT")
  val coolantPressure = new ChannelType[Pressure]("water pressure", "water pres", "water P", "WP")
  val ignitionAdvance = new ChannelType[Angle]("spark advance", "spark adv", "spark", "SA")
  val injectorPulseWidth = new ChannelType[Time]("injector pulse width", "injector pulse", "inject pulse", "IPW")
  val injectorDutyCycle = new ChannelType[DNil]("injector duty cycle", "injector duty", "inject duty", "IDC")
  val fuelLevel = new ChannelType[DNil]("fuel level", "fuel lvl", "fuel", "FL")  

  // val types =
    // Set(rpm, oilPressure, oilTemperature, coolantTemperature, coolantPressure, ignitionAdvance, injectorPulseWidth, injectorDutyCycle)
}