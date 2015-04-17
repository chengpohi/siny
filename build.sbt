name := "siny"

version := "1.0"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.9",
    "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0-M5",
    "com.typesafe.akka" % "akka-http-experimental_2.11" % "1.0-M5",
    "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "1.0-M5",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0-M5",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.8",
    "com.typesafe.akka" %% "akka-remote" % "2.3.8"
  )
}

resolvers ++= Seq(
)
