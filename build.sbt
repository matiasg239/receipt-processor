val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.10"
val MunitVersion = "1.1.0"
val LogbackVersion = "1.5.16"
val MunitCatsEffectVersion = "2.0.0"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.matiasg239",
    name := "receipt-processor",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.3",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % "0.14.8",
      "io.circe" %% "circe-literal" % "0.14.8",
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime,
      "org.scalatest" %% "scalatest" % "3.2.19" % "test",
      "org.scalatest" %% "scalatest-funsuite" % "3.2.19" % "test"
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
