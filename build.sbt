name := "scala_coaching"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalatest"  %% "scalatest"  % "3.2.0"  % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"

libraryDependencies += "org.typelevel" %% "cats-core"   % "2.1.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0-26ef642"

libraryDependencies += "dev.zio" %% "zio"              % "1.0.0"
libraryDependencies += "dev.zio" %% "zio-streams"      % "1.0.0"
libraryDependencies += "dev.zio" %% "zio-test"         % "1.0.0" % "test"
libraryDependencies += "dev.zio" %% "zio-test-sbt"     % "1.0.0" % "test"
libraryDependencies += "dev.zio" %% "zio-kafka"        % "0.12.0"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.1.4.0"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
