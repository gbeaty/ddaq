package ddaq.displayController.controllers

import ddaq._
import ddaq.trigger._
import ddaq.displayController._

import scalaz._
import Scalaz._

object Test extends DisplayController {
	val name = "Test"
	val refreshRate = 0.2f
}