package ddaq

import Ddaq._

trait Format {
  val name: String  
  def header(cs: Set[Channel[_]]): String

  def fileExtension: Option[String] = None  
  def footer(cs: Set[Channel[_]]) = ""

  lazy val fileEnd = fileExtension.map("." + _)
}

trait Logger {
  val filename: String
  val format: Format
  val channels: Set[Channel[_]]
}
object Logger {
  def apply(fn: String, f: Format, cs: Set[Channel[_]]) = new Logger {
    val format = f
    val channels = cs

    val filename = format.fileEnd match {
      case Some(fe) if(!fn.endsWith(fe)) => fn + fe
      case _ => fn
    }
  }
}