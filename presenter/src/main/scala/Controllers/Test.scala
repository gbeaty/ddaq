package ddaq.controller

import ddaq._

import scalaz._
import Scalaz._

object PresentEverything extends Presenter("everything") {
  def out(in: Set[Channel[_]]) = in.head
}