val ScalaVer = "2.12.6"

val Cats           = "1.1.0"
val CatsEffect     = "1.0.0-RC2-93ac33d"
val Doobie         = "0.5.3"
val Http4s         = "0.18.15"
val Circe          = "0.9.3"
val KindProjector  = "0.9.7"

val SLF4J          = "1.7.25"

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
    // , "-Xlint"
    // , "-Yinline-warnings"
    , "-Ywarn-dead-code"
    , "-Xfuture"
    , "-Ypartial-unification")

, libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"   % Cats
  , "org.typelevel" %% "cats-effect" % CatsEffect

  , "org.tpolecat" %% "doobie-core"     % Doobie
  , "org.tpolecat" %% "doobie-postgres" % Doobie
  
  , "org.http4s" %% "http4s-dsl"          % Http4s
  , "org.http4s" %% "http4s-circe"        % Http4s
  , "org.http4s" %% "http4s-blaze-server" % Http4s
  , "org.http4s" %% "http4s-blaze-client" % Http4s

  , "io.circe" %% "circe-core"    % Circe
  , "io.circe" %% "circe-generic" % Circe
  , "io.circe" %% "circe-parser"  % Circe

  , "org.slf4j" % "slf4j-simple" % SLF4J
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)

