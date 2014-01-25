package ddaq.displayController

import org.specs2.mutable._
import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import scalaz.stream._

object StartHere extends Specification {

  val seq = Seq(0,2,4,6)
  val one: Process[Task,Int] = Process.range(0,10,2)
  val two: Process[Task,Int] = Process.range(1,7,2)
  val merge = one merge two

  val both = one.map((1,_)).merge(two.map { e => (2,e) }).fold[Map[Int,Int]] { tup =>
    println(tup)
    val (source, newValue) = tup
    Map(source -> newValue)
  }

  /*val both = .foldMap(Option[Int],Option[Int]) { (acc, el) =>
    (acc, el) match {
      case ()
    }
  }*/

  /*def both[A,B]: Wye[A, B, (A,B)] = {
    import wye._
    import Process._
    import ReceiveY._
    def go(a: A): Wye[A, Any, A] =
      receiveBoth({
        case ReceiveL(l)  => emit(l) fby go(l)
        case ReceiveR(_)  => emit(a) fby go(a)
        case HaltOne(rsn) => Halt(rsn)
      })
    awaitL[A].flatMap(s => emit(s) fby go(s))
  }*/

  "Notifications" should {
    "Merge" in {
      merge.runLog.run.toSeq ==== scala.collection.immutable.Seq(0,1,2,3,4,5,6,8)
    }
    "Combine" in {
      both.runLog.run.toSeq ==== scala.collection.immutable.Seq[Map[Int,Int]]() //scala.collection.immutable.Seq((0,1),(2,1),(2,3),(4,3),(4,5),(6,5),(8,5))
    }
  }
}