package zio_learning

import java.io.IOException

import zio._
import zio.console._

object MyApp extends zio.App {

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    myAppLogic.exitCode

  val myAppLogic: ZIO[Console, IOException, Unit] =
    for {
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to ZIO!")
    } yield ()
}
