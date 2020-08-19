package zio_learning

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.{ IO, UIO }

class HandlingResourcesTests extends AnyFunSuiteLike with Matchers {
  test("test Handling Resources.") {
    val finalizer =
      UIO.effectTotal(println("Finalizing!"))
    // finalizer: UIO[Unit] = zio.ZIO$EffectTotal@54646a0e
    println(finalizer)

    val finalized: IO[String, Unit] =
      IO.fail("Failed!").ensuring(finalizer)
    // finalized: IO[String, Unit] = zio.ZIO$CheckInterrupt@4f4adf76
    println(finalized)
  }
}
