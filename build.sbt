name := "encry-common"

version := "0.1.5"

scalaVersion := "2.12.6"

organization := "org.encry"

resolvers ++= Seq("Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "SonaType" at "https://oss.sonatype.org/content/groups/public",
  "Typesafe maven releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

val circeVersion = "0.9.3"

val apiDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
)

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "21.+",
  "org.scorexfoundation" %% "scrypto" % "2.1.+",
  "org.encry" %% "prism" % "0.2.7",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
) ++ apiDependencies

licenses in ThisBuild := Seq("GNU GPL 3.0" -> url("https://github.com/EncryFoundation/EncryCommon/blob/master/LICENSE"))

homepage in ThisBuild := Some(url("https://github.com/EncryFoundation/EncryCommon"))

publishMavenStyle in ThisBuild := true

publishArtifact in Test := false

publishTo in ThisBuild :=
  Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)

pomExtra in ThisBuild :=
  <scm>
    <url>git@github.com:EncryFoundation/EncryCommon.git</url>
    <connection>scm:git:git@github.com:EncryFoundation/EncryCommon.git</connection>
  </scm>
  <developers>
    <developer>
      <id>Oskin1</id>
      <name>Ilya Oskin</name>
    </developer>
  </developers>