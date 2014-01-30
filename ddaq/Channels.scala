import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import scalaz.stream._

import org.joda.time._

package object ddaq {

  type Source[A] = Process[Task,Sample[A]]
  private type CS[A] = Map[Source[A],Sample[A]]

  object Sample {
    def apply[A](v: A, ts: DateTime) = new Sample[A] {
      val value = v
      val timestamp = ts
    }

    implicit def orderer[A] = Order.order[Sample[A]] { (s1,s2) =>
      if(s1.timestamp.getMillis > s2.timestamp.getMillis)
        Ordering.GT
      else if(s2.timestamp.getMillis < s2.timestamp.getMillis)
        Ordering.LT
      else Ordering.EQ
    }
  }

  trait Sample[A] {
    val value: A
    val timestamp: DateTime
    def map[B](f: A => B) = Sample(f(value), timestamp)
  }  
  trait CombinedSample[A] extends Sample[CS[A]] {
    val value: CS[A]
    val timestamp = value.map(_._2.timestamp).maxBy(_.getMillis)
  }
  implicit def toCombinedSample[A](in: CS[A]) = new CombinedSample[A] { val value = in }
  object CombinedSample {
    def apply[A](v: CS[A], t: DateTime) = new CombinedSample[A] {
      val value = v
      override val timestamp = t
    }
  }

  object Channels {
    import Sample._    

    implicit def mapoid[A] = new Monoid[CombinedSample[A]] {
      def zero = Map[Source[A],Sample[A]]()
      def append(f1: CombinedSample[A], f2: => CombinedSample[A]) = f1.value ++ f2.value
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
}