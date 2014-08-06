package ddaq

import ddaq._

trait Display {
	val name: String

	def apply[A](chanName: String, sample: Option[Sample[A]])
}