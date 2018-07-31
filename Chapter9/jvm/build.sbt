val ScalaVer = "2.12.6"

val Cats          = "1.2.0"
val CatsEffect    = "1.0.0-RC2-93ac33d"
val KindProjector = "0.9.7"

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
    "org.typelevel" %% "cats-core"   % Cats
  , "org.typelevel" %% "cats-effect" % CatsEffect
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)

