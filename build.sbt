name := "encry-common"
version := "0.8.2"
scalaVersion := "2.12.6"
organization := "org.encry"

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "org.encry" %% "prism" % "0.8.2",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

resolvers ++= Seq("Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "SonaType" at "https://oss.sonatype.org/content/groups/public",
  "Typesafe maven releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

licenses in ThisBuild := Seq("GNU GPL 3.0" -> url("https://github.com/EncryFoundation/EncryCommon/blob/master/LICENSE"))

homepage in ThisBuild := Some(url("https://github.com/EncryFoundation/EncryCommon"))

publishMavenStyle in ThisBuild := true

publishArtifact in Test := false

publishTo in ThisBuild := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)

pomExtra in ThisBuild :=
  <scm>
    <url>git@github.com:EncryFoundation/EncryCommon.git</url>
    <connection>scm:git:git@github.com:EncryFoundation/EncryCommon.git</connection>
  </scm>
    <developers>
      <developer>
        <id>kapinuss</id>
        <name>Stanislav Kapinus</name>
      </developer>
      <developer>
        <id>Oskin1</id>
        <name>Ilya Oskin</name>
      </developer>
      <developer>
        <id>Bromel777</id>
        <name>Alexander Romanovskiy</name>
      </developer>
    </developers>