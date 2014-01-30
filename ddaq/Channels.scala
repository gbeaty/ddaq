package ddaq

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

object Channels {
  import Sample._

  type Source[A] = Process[Task,Sample[A]]
  private type CS[A] = Map[Source[A],Sample[A]]   

  implicit def mapoid[A] = new Monoid[CombinedSample[A]] {
    def zero = Map[Source[A],Sample[A]]()
    def append(f1: CombinedSample[A], f2: => CombinedSample[A]) =
      f2.value.foldLeft(f1.value) { (res,kv) =>
        val (k,v) = kv
        f1.value.get(k) match {
          case Some(prev) if(v.timestamp.getMillis > prev.timestamp.getMillis) => res + kv
          case _ => res
        }
      }
  }

  def combine[A](sources: Source[A]*) = sources.map { s =>
      s.map((s,_))
    }.foldLeft(Process.emitSeq[Task,(Source[A],Sample[A])](Nil)) { (acc, process) =>
      acc.merge(process)
    }

  def lasts[A](sources: Source[A]*) = combine(sources: _*).scanMap[CombinedSample[A]] { sampTup =>
    val (source, sample) = sampTup
    Map(source -> sample)
  }
}
import Channels._

trait Channel[A] {
  val source: Source[A]
  private val _source = source

  def combine(channels: Channel[A]*) = Channels.lasts(channels.map(_.source): _*)
  def call(n: String) = new NamedChannel[A] {
    val name = n
    val source = _source
  }
}
trait NamedChannel[A] extends Channel[A] {
  val name: String
}
trait CombinedChannel[A] extends Channel[CombinedSample[A]]