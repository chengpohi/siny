name := "siny"

version := "1.0"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= {
  Seq(
    "io.netty" % "netty" % "3.10.1.Final",
    "com.typesafe.akka" %% "akka-actor" % "2.3.8",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.8",
    "com.typesafe.akka" %% "akka-remote" % "2.3.8",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}

resolvers ++= Seq(
)
