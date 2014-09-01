package ddaq

import scalaz._
import Scalaz._

abstract class Presenter(val name: String) {
  def out(in: Set[Channel[_]]): Channel[_]
}