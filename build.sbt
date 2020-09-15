import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.thecookiezen"
ThisBuild / organizationName := "thecookiezen.com"

lazy val root = (project in file("."))
  .settings(
    name := "bank-kata-scala",
    libraryDependencies += scalaTest % Test
  )

