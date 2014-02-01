package ddaq.display

import ddaq._

trait Display {
	val name: String
	val platforms: Set[Platform]

	def apply[A](chanName: String, sample: Option[Sample[A]])
}