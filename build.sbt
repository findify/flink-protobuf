name := "flink-protobuf"

version := "0.2"

scalaVersion := "2.12.14"

lazy val scalapbVersion = "0.11.3"
lazy val flinkVersion   = "1.13.0"

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime"       % scalapbVersion % "protobuf,test,compile",
  "com.google.protobuf"   % "protobuf-java"         % "3.17.1"       % "protobuf,test,compile",
  "org.apache.flink"     %% "flink-scala"           % flinkVersion   % "provided",
  "org.apache.flink"     %% "flink-streaming-scala" % flinkVersion   % "provided",
  "org.apache.flink"     %% "flink-test-utils"      % flinkVersion   % "test",
  "org.scalatest"        %% "scalatest"             % "3.2.9"        % "test"
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
