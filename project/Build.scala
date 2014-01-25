package racewatcher

import sbt._
import Keys._

object Ddaq extends Build {

  val appVersion      = "0.1"
  val scala = "2.10.3"

  val jodas = Seq("joda-time" % "joda-time" % "2.2", "org.joda" % "joda-convert" % "1.2")
  val shapeless = "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full

  val ddaqDeps = Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.5",
    "org.scalaz.stream" %% "scalaz-stream" % "0.3",
    "org.specs2" %% "specs2" % "2.3.7" % "test"
  ) ++ jodas

  val commonResolvers = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "releases"  at "http://oss.sonatype.org/content/repositories/releases",
    "spray repo" at "http://repo.spray.io",
    Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    "Pellucid Bintray"  at "http://dl.bintray.com/content/pellucid/maven",
    "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  )

  scalacOptions in Test ++= Seq("-Yrangepos")

  def project(name: String) = sbt.Project(
    name,
    base = file(name),
    settings = Defaults.defaultSettings ++ Seq(
      scalaVersion := scala,
      resolvers ++= commonResolvers,
      version := appVersion,
      libraryDependencies := ddaqDeps
    )
  )  

  lazy val ddaq = project("ddaq")

  def subproject(name: String) = project(name).dependsOn(ddaq)

  lazy val displayController = subproject("display-controller")

  lazy val logger = subproject("logger")
}