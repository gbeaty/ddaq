package ddaq.controller

import ddaq._
import Ddaq._
import ddaq.Channel._

import scalaz._
import Scalaz._

/*
  Use cases:
    Single channel - oil temp
    Multi channel associated with one channel - oil pressure and rpm
*/

case class Notification(name: String, triggerLevels: Map[Trigger,Byte]) {
  val triggers = triggerLevels.keySet
  val channels = triggers.map(_.channels).flatten

  val levelStream = triggerLevels.map { tup =>
    val (trigger, level) = tup
    tup
  }

  // val levelEnum = EnumeratorP.mergeAll(triggers.map(_.enum).toSeq: _*)

  /* val levelEnum: EnumeratorP[Sample[Byte],Id.Id] = triggers.foldLeft(EnumeratorP.empty[Sample[Boolean],Id.Id]){ (a,e) =>
    e.enum.merge(a)
  } */
}

object Notification {
	def greaterThan[A](channel: Channel[A], first: A, second: Option[A] = None, third: Option[A] = None) = {

	}
}