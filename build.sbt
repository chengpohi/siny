name := "siny"

version := "1.0"

scalaVersion := "2.11.3"

unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= {
  Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.7",
    "com.typesafe.akka" %% "akka-actor" % "2.3.8",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.8",
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.5.2",
    "org.json4s" %% "json4s-native" % "3.2.10",
    "org.json4s" %% "json4s-jackson" % "3.2.10",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}

resolvers ++= Seq(
)
