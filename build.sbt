name := "scala_coaching"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalatest"  %% "scalatest"  % "3.2.0"  % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"

libraryDependencies += "org.typelevel" %% "cats-core"   % "2.1.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0-26ef642"

libraryDependencies += "dev.zio" %% "zio"         % "1.0.0"
libraryDependencies += "dev.zio" %% "zio-streams" % "1.0.0"
