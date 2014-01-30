package ddaq.displayController

import ddaq._
import ddaq.trigger._

import scalaz._
import Scalaz._

trait DisplayController {
	val name: String
	val refreshRate: Float
}