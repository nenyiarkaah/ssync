import Unit._
import scala.sys.process.Process

lazy val scalatestVersion = "3.2.15"
lazy val loggingVersion = "3.9.5"
lazy val pureconfigVersion = "0.17.2"
lazy val mockitoVersion = "1.17.14"
lazy val betterFilesVersion = "3.9.2"
lazy val logbackVersion = "1.3.5"
lazy val scalacticVersion = "3.2.16"
lazy val scalaMockVersion = "5.2.0"
lazy val slf4jVersiion = "2.0.7"
val macwireVersion = "2.5.8"

lazy val root = (project in file("."))
  .configs(UnitTest, IntegrationTest)
  .settings(
    unitSettings,
    Defaults.itSettings,
    dockerBuildxSettings,
    ThisBuild / organization := "org.ssync",
    ThisBuild / scalaVersion := "2.13.11",
    name := "ssync",
    ThisBuild / version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % "it,test",
      "org.scalatest" %% "scalatest-flatspec" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalaMockVersion % Test,
      "org.mockito" % "mockito-scala-cats_2.13" % mockitoVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % loggingVersion,
      "org.scalactic" %% "scalactic" % scalacticVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.slf4j" % "slf4j-api" % slf4jVersiion,
      "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
      "com.github.pathikrit" % "better-files_2.13" % betterFilesVersion,
      "com.softwaremill.macwire" %% "macros" % macwireVersion
    ).map(_.exclude("org.slf4j", "*")),
    dockerBaseImage := "eclipse-temurin:8u345-b01-jre-jammy",
    Docker / packageName := "ssync",
    dockerRepository := sys.env.get("REGISTRY"),
    dockerUpdateLatest := true,
    dockerBuildOptions := Seq("--force-rm", "-t", "[dockerAlias]", "--platform=linux/arm,linux/amd64"),
    Test / fork := true,
    addCompilerPlugin("org.typelevel" % "kind-projector_2.13.0" % "0.13.2")
  )
  .enablePlugins(JavaAppPackaging, AshScriptPlugin)
  .enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, buildInfoBuildNumber, scalaVersion, sbtVersion),
    buildInfoPackage := "org.ssync.info",
    buildInfoOptions += BuildInfoOption.ToJson,
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature"
)

// Create a test Scala style task to run with tests
lazy val testScalaStyle = taskKey[Unit]("testScalaStyle")
testScalaStyle := (Test / scalastyle).toTask("").value
(Test / test) := ((Test / test) dependsOn testScalaStyle).value
(Test / scalastyleConfig) := baseDirectory.value / "project" / "scalastyle-config.xml"

lazy val compileScalaStyle = taskKey[Unit]("compileScalaStyle")
compileScalaStyle := (Compile / scalastyle).toTask("").value
(Test / test) := ((Test / test) dependsOn compileScalaStyle).value
(Compile / scalastyleConfig) := baseDirectory.value / "project" / "scalastyle-config.xml"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val ensureDockerBuildx = taskKey[Unit]("Ensure that docker buildx configuration exists")
lazy val dockerBuildWithBuildx = taskKey[Unit]("Build docker images using buildx")
lazy val dockerBuildxSettings = Seq(
  ensureDockerBuildx := {
    if (Process("docker buildx inspect multi-arch-builder").! == 1) {
      Process("docker buildx create --use --name multi-arch-builder", baseDirectory.value).!
    }
  },
  dockerBuildWithBuildx := {
    streams.value.log("Building and pushing image with Buildx")
    dockerAliases.value.foreach(
      alias => Process("docker buildx build --platform=linux/arm,linux/arm64,linux/amd64 --push -t " +
        alias + " .", baseDirectory.value / "target" / "docker"/ "stage").!
    )
  },
  Docker / publish := Def.sequential(
    Docker / publishLocal,
    ensureDockerBuildx,
    dockerBuildWithBuildx
  ).value
)
