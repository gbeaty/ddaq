package ddaq

import scala.reflect.runtime.universe.TypeTag

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
    def zero = Map[Input[A],Sample[A]]()
    def append(f1: Combined[A], f2: => Combined[A]) =
      f2.foldLeft(f1) { (res,kv) =>
        val (k,v) = kv
        f1.get(k) match {
          case Some(prev) if(v.timestamp.getMillis > prev.timestamp.getMillis) => res + kv
          case _ => res
        }
      }
  }

  def combine[A](sources: Input[A]*) = sources.map { s =>
      s.map((s,_))
    }.foldLeft(Process.emitSeq[Task,(Input[A],Sample[A])](Nil)) { (acc, process) =>
      acc.merge(process)
    }

  def lasts[A](sources: Input[A]*) = combine(sources: _*).scanMap[Combined[A]] { sampTup =>
    val (stream, sample) = sampTup
    Map(stream -> sample)
  }
}

class Named(_names: String*) {
  val names = _names.sortBy(-_.length).toSet.toArray
  def getLongestName(l: Int) = {
    val len = if(l < 1) 1 else l
    names.find(_.length <= len).getOrElse(names.last.take(len))
  }
}

class ChannelType[A](names: String*)(implicit val typeTag: TypeTag[A]) extends Named(names: _*)
object ChannelType {
  val other = new ChannelType("other","?","")
}

case class Channel[A](in: Input[A], typ: ChannelType[A])