package zio_learning

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.{ IO, ZIO, _ }

class HandlingErrorsTests extends AnyFunSuiteLike with Matchers {
  test("test Handling Errors.") {
    val zeither: UIO[Either[String, Int]] =
      IO.fail("Uh oh!").either

    def sqrt(io: UIO[Double]): IO[String, Double] =
      ZIO.absolve(
        io.map(value =>
          if (value < 0.0) Left("Value must be >= 0.0")
          else Right(Math.sqrt(value))
        )
      )
  }
}
