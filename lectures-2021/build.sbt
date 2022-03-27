// The simplest possible sbt build file is just one line:

scalaVersion := "2.12.8"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings" Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.12.8"

// It's possible to define many kinds of settings, such as:

name := "ysc4231"
organization := "edu.yale-nus"
version := "1.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "commons-io" % "commons-io" % "2.4"
)

// Adding actors
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.26"
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.26"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.26" % Test

scalacOptions ++= Seq()

// This prevents individual tests executing in parallel,
// thus, messing up the ThreadID logic
parallelExecution in ThisBuild := false
