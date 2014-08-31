package ddaq

import Ddaq._

import scunits._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

object Channel {
  import Sample._

  implicit def mapoid[A] = new Monoid[Combined[A]] {
    def zero = Map[Ddaq.Channel[A],Sample[A]]()
    def append(f1: Combined[A], f2: => Combined[A]) =
      f2.foldLeft(f1) { (res,kv) =>
        val (k,v) = kv
        f1.get(k) match {
          case Some(prev) if(v.timestamp.getMillis > prev.timestamp.getMillis) => res + kv
          case _ => res
        }
      }
  }

  def combine[A](sources: Ddaq.Channel[A]*) = sources.map { s =>
      s.map((s,_))
    }.foldLeft(Process.emitSeq[Task,(Ddaq.Channel[A],Sample[A])](Nil)) { (acc, process) =>
      acc.merge(process)
    }

  def lasts[A](sources: Ddaq.Channel[A]*) = combine(sources: _*).scanMap[Combined[A]] { sampTup =>
    val (stream, sample) = sampTup
    Map(stream -> sample)
  }
}

class Named(_names: String*) {
  val names = _names.sortBy(-_.length).toArray
  def getLongestName(l: Int) = {
    val len = if(l < 1) 1 else l
    names.find(_.length <= len).getOrElse(names.last.take(len))
  }
}

class NamedChannel[A](_names: String*)(val channel: Ddaq.Channel[A]) extends Named(_names: _*)

class ChannelType[A <: Dims](_names: String*) extends Named(_names: _*)

case class ChannelComposer(ins: Set[NamedChannel[_]], outs: Set[NamedChannel[_]])