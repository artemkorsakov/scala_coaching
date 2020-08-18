package zio

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.internal.Platform

class RunningEffectsTests extends AnyFunSuiteLike with Matchers {
  test("test Running Effects.") {
    val runtime = Runtime.default
    runtime.unsafeRun(ZIO(println("Hello World!")))

    val myRuntime: Runtime[Int] = Runtime(42, Platform.default)
    myRuntime.unsafeRun(ZIO(println("Hello World!")))
  }
}
