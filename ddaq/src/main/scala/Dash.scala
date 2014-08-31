package ddaq

import ddaq._

trait Dash {
	val name: String
  val idealChannelCount: Int
  val maxChannelCount: Int

	def apply[A](chanName: String, sample: Option[Sample[A]])
}