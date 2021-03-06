import sbt._
import Keys._

object Ddaq extends Build {

  val appVersion = "0.0.1"
  val scala = "2.11.2"

  val jodas = Seq("joda-time" % "joda-time" % "2.4", "org.joda" % "joda-convert" % "1.7")
  val shapeless = "com.chuusai" % "shapeless" % "2.0.0" cross CrossVersion.full

  val ddaqDeps = Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "org.scalaz.stream" %% "scalaz-stream" % "0.4.1",
    "org.specs2" %% "specs2" % "2.4" % "test",
    "com.chuusai" % "shapeless_2.10.4" % "2.0.0",
    "org.scunits" %% "core" % "0.0.1-SNAPSHOT"
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

  val ddaq = project("ddaq")

  def subproject(name: String) = project(name).dependsOn(ddaq)

  lazy val presenter = subproject("presenter")
  lazy val logger = subproject("logger")
  lazy val sensor = subproject("sensor")
  lazy val source = subproject("source")
  lazy val channel = subproject("channel")

  def platform(name: String) = project(name).dependsOn(ddaq, presenter, logger, sensor, source, channel)

  lazy val test = platform("test")
  lazy val android = platform("android")  

  override def rootProject = Some(test)
}