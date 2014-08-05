package ddaq.sensors.aem

import ddaq.sensors._

object Pressure {
  lazy val vouts = Range.Double(0.5, 4.5, 0.25)

  lazy val psig100 = Sensor.lookup(
    "AEM 30-2131-100",
    Seq(0, 6.25, 12.5, 18.75, 25, 31.25, 37.5, 43.75, 50, 56.25, 62.5, 68.75, 75, 81.25, 87.5, 93.75, 100).zip(vouts).toMap
  )

  lazy val psig15 = Sensor.lookup(
    "AEM 30-2131-15G",
    Seq(0, 0.9, 1.9, 2.8, 3.8, 4.7, 5.6, 6.6, 7.5, 8.4, 9.4, 10.3, 11.3, 12.2, 13.1, 14.1, 15.0).zip(vouts).toMap
  )
}

object Temperature {
  lazy val vouts = Range.Double(-40.0, 150, 5.0)

  lazy val t302011 = Sensor.lookup(
    "AEM 30-2011",
    Seq(
      102122,73340,53249,39064,28939,21637,16321,12413,9516,7354,5728,4496,3555,
      2830, 2268, 1828, 1483, 1210, 992, 819, 679, 566, 475, 400, 338, 287,
      244.8, 209.7, 180.3, 155.6, 134.7, 117.1, 102.2, 89.4, 78.5, 69.1, 61.1, 54.1, 48.1
    ).zip(vouts).toMap
  )
}