package ddaq.channel

import ddaq._

package object ice {
  private def ct(ns: String*) = new ChannelType(ns: _*)
  val rpm = ct("rpm")
  val oilPressure = ct("oil pressure", "oil pres", "oil P", "OP")
  val oilTemperature = ct("oil temperature", "oil temp", "oil T", "OT")
  val coolantTemperature = ct("water temperature", "water temp", "water T", "WT")
  val coolantPressure = ct("water pressure", "water pres", "water P", "WP")
  val ignitionAdvance = ct("spark advance", "spark adv", "spark", "SA")
  val injectorPulseWidth = ct("injector pulse width", "injector pulse", "inject pulse", "IPW")
  val injectorDutyCycle = ct("injector duty cycle", "injector duty", "inject duty", "IDC")
  val fuelLevel = ct("fuel level", "fuel lvl", "fuel", "FL")
  val fuelEconomy = ct("fuel economy", "fuel econ", "FE")

  val types =
    Set(rpm, oilPressure, oilTemperature, coolantTemperature, coolantPressure, ignitionAdvance, injectorPulseWidth, injectorDutyCycle)
}