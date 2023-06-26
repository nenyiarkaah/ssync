import sbt.Keys._
import sbt._

object Unit {
  final val UnitTest = Configuration.of("UnitTest", "ut") extend (Test)
  final val unitSettings =
    inConfig(UnitTest)(unitConfig)
  lazy val unitConfig =
    Defaults.configSettings ++ Defaults.testTasks ++ Seq(
      UnitTest / fork := false,
      UnitTest / parallelExecution := false,
      Test / parallelExecution := false,
      UnitTest / scalaSource := baseDirectory.value / "src" / "test" / "scala",
      Test / envVars := Map("PROJECT_ENV" -> "test")
    )
}
