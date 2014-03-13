package ddaq

sealed trait Platform
case object Test extends Platform
case object Android extends Platform

object Platform {
	val all = Set(Android)
	val test = Test
}