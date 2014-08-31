package ddaq

import scalaz._
import Scalaz._

trait Presenter {
	val name: String
	val refreshRate: Float
}