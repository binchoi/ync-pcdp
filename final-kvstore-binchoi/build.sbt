name := "key-value-store"
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
