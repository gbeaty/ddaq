package ddaq

package object implicits {
	implicit def toSome[A](a: A) = Some(a)
}