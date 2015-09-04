###Plugin

`touch` file `~/.sbt/0.13/plugins/gpg.sbt` and add:

```
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
```

`cd` to project home and open ***sbt*** console.

```
pgp-cmd gen-key
```

###SBT File

append the follow content.

```
publishMavenStyle := true
publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

```

###Sonatype credentials

`touch` file `~/.sbt/0.13/sonatype.sbt` and add:

```
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "oss.sonatype.org",
                           "username",
                           "password")
```

###Release

```
publishSigned
```
