package zio

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers

class BasicConcurrencyTests extends AnyFunSuiteLike with Matchers {
  val runtime: Runtime[zio.ZEnv] = Runtime.default

  test("test Basic Concurrency.") {
    def fib(n: Long): UIO[Long] =
      UIO {
        if (n <= 1) UIO.succeed(n)
        else fib(n - 1).zipWith(fib(n - 2))(_ + _)
      }.flatten

    val fib100Fiber: UIO[Fiber[Nothing, Long]] =
      for {
        fiber <- fib(100).fork
      } yield fiber

    println(fib100Fiber)

    val mes = for {
      fiber   <- IO.succeed("Hi!").fork
      message <- fiber.join
    } yield message

    println(mes)

    for {
      fiber <- IO.succeed("Hi!").forever.fork
      exit  <- fiber.interrupt
    } yield exit

    for {
      fiber <- IO.succeed("Hi!").forever.fork
      _     <- fiber.interrupt.fork // I don't care!
    } yield ()

    for {
      fiber1 <- IO.succeed("Hi!").fork
      fiber2 <- IO.succeed("Bye!").fork
      fiber = fiber1.zip(fiber2)
      tuple <- fiber.join
    } yield tuple

    for {
      fiber1 <- IO.fail("Uh oh!").fork
      fiber2 <- IO.succeed("Hurray!").fork
      fiber = fiber1.orElse(fiber2)
      tuple <- fiber.join
    } yield tuple

    import zio.duration._

    IO.succeed("Hello").timeout(10.seconds)
  }
}
