name := "flink-protobuf"

version := "0.3"

scalaVersion := "2.12.14"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val scalapbVersion = "0.11.3+12-3a9f2017+20210601-1710-SNAPSHOT"
lazy val flinkVersion   = "1.13.1"

libraryDependencies ++= Seq(
  "io.findify"         %% "scalapb-runtime"       % scalapbVersion % "protobuf,test,compile",
  "com.google.protobuf" % "protobuf-java"         % "3.17.1"       % "protobuf,test,compile",
  "org.apache.flink"   %% "flink-scala"           % flinkVersion   % "provided",
  "org.apache.flink"   %% "flink-streaming-scala" % flinkVersion   % "provided",
  "org.apache.flink"   %% "flink-test-utils"      % flinkVersion   % "test",
  "org.scalatest"      %% "scalatest"             % "3.2.9"        % "test"
)

Test / PB.targets := Seq(
  PB.gens.java                         -> (Test / sourceManaged).value,
  scalapb.gen(javaConversions = false) -> (Test / sourceManaged).value
)

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-protobuf"))
publishMavenStyle := true
publishTo := sonatypePublishToBundle.value
scmInfo := Some(
  ScmInfo(
    url("https://github.com/findify/flink-protobuf"),
    "scm:git@github.com:findify/flink-protobuf.git"
  )
)
developers := List(
  Developer(id = "romangrebennikov", name = "Roman Grebennikov", email = "grv@dfdx.me", url = url("https://dfdx.me/"))
)

publishLocalConfiguration / publishMavenStyle := true
