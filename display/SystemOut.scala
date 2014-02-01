package ddaq.display.displays

import ddaq._
import ddaq.display._

object SystemOut extends Display {
	val name = "System.out"
	val platforms: Set[Platform] = Set(Test)

	def apply[A](chanName: String, sample: Option[Sample[A]]) {
		print(chanName + ": " + sample.map(_.value).getOrElse("----"))
	}
}