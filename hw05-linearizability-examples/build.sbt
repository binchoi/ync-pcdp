name := "theory-03-examples"
organization := "edu.yale-nus"
version := "1.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "commons-io" % "commons-io" % "2.4"
)

scalacOptions ++= Seq()

// This prevents individual tests executing in parallel,
// thus, messing up the ThreadID logic
parallelExecution in ThisBuild := false
