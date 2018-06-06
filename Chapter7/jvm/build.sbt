val ScalaVer = "2.12.3"

val Cats          = "0.9.0"
val CatsEffect    = "0.3"
val KindProjector = "0.9.4"

val ScalaTest  = "3.0.4"
val ScalaCheck = "1.13.5"

val Akka = "2.5.12"

lazy val commonSettings = Seq(
  name    := "jvm"
, version := "0.1.0"
, scalaVersion := ScalaVer

, addCompilerPlugin("org.spire-math" %% "kind-projector" % KindProjector)
, scalacOptions ++= Seq(
      "-deprecation"
    , "-encoding", "UTF-8"
    , "-feature"
    , "-language:existentials"
    , "-language:higherKinds"
    , "-language:implicitConversions"
    , "-language:experimental.macros"
    , "-unchecked"
    // , "-Xfatal-warnings"
    , "-Xlint"
    // , "-Yinline-warnings"
    , "-Ywarn-dead-code"
    , "-Xfuture"
    , "-Ypartial-unification")

, libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % Akka
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import jvm._, Main._"
  )
