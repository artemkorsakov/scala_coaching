package zio

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.console._

class BasicOperationTests extends AnyFunSuiteLike with Matchers {
  test("test Basic Operation.") {
    val succeeded: UIO[Int] = IO.succeed(21).map(_ * 2)
    println(succeeded)

    val failed: IO[Exception, Unit] = IO.fail("No no!").mapError(msg => new Exception(msg))
    println(failed)

    val sequenced = getStrLn.flatMap(input => putStrLn(s"You entered: $input"))
    println(sequenced)

    val program = {
      for {
        _    <- putStrLn("Hello! What is your name?")
        name <- getStrLn
        _    <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
      } yield ()
    }

    val zipped: UIO[(String, Int)] =
      ZIO.succeed("4").zip(ZIO.succeed(2))

    val zipRight1 =
      putStrLn("What is your name?").zipRight(getStrLn)

    val zipRight2 =
      putStrLn("What is your name?") *>
      getStrLn
  }
}
