organization := "com.github.chengpohi"

name := "siny"

version := "1.1"

publishMavenStyle := true

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
    "com.google.guava" % "guava" % "18.0",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}

resolvers ++= Seq(
)


publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}


pomExtra := (
  <url>https://github.com/chengpohi/siny</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:chengpohi/siny.git</url>
    <connection>scm:git:git@github.com:chengpohi/siny.git</connection>
  </scm>
  <developers>
    <developer>
      <id>chengpohi</id>
      <name>chengpohi</name>
      <url>https://github.com/chengpohi/siny</url>
    </developer>
  </developers>
)
