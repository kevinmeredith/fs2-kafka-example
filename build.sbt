
ThisBuild / scalaVersion := "2.12.13"

scalacOptions += "-Ypartial-unification"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "com.github.fd4s" %% "fs2-kafka" % "2.2.0",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.0" % IntegrationTest,
      "org.testcontainers" % "kafka" % "1.16.2" % IntegrationTest
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )