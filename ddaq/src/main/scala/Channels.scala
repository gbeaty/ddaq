package ddaq

import ddaq.implicits._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

object Channel {
  import Sample._

  type Channel[A] = Process[Task,Sample[A]]  

  implicit def mapoid[A] = new Monoid[Combined[A]] {
    def zero = Map[Channel[A],Sample[A]]()
    def append(f1: Combined[A], f2: => Combined[A]) =
      f2.foldLeft(f1) { (res,kv) =>
        val (k,v) = kv
        f1.get(k) match {
          case Some(prev) if(v.timestamp.getMillis > prev.timestamp.getMillis) => res + kv
          case _ => res
        }
      }
  }

  def combine[A](sources: Channel[A]*) = sources.map { s =>
      s.map((s,_))
    }.foldLeft(Process.emitSeq[Task,(Channel[A],Sample[A])](Nil)) { (acc, process) =>
      acc.merge(process)
    }

  def lasts[A](sources: Channel[A]*) = combine(sources: _*).scanMap[Combined[A]] { sampTup =>
    val (stream, sample) = sampTup
    Map(stream -> sample)
  }
}