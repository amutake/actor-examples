import sbt._
import sbt.Keys._

object Build extends sbt.Build {

  lazy val project = Project(
    id = "basic-example",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name                  := "basic-example",
      organization          := "io.github.amutake",
      version               := "0.1-SNAPSHOT",
      scalaVersion          := "2.11.1",
      scalacOptions         := Seq("-deprecation", "-feature", "-encoding", "utf8"),
      libraryDependencies   ++= Dependencies()
    )
  )

  object Dependencies {

    object Versions {
      val akka = "2.3.4"
    }

    val compileDependencies = Seq(
      "com.typesafe.akka" %% "akka-actor" % Versions.akka
    )

    val testDependencies = Seq(
      "com.typesafe.akka" %% "akka-testkit" % Versions.akka % "test"
    )

    def apply(): Seq[ModuleID] = compileDependencies ++ testDependencies

  }

}
