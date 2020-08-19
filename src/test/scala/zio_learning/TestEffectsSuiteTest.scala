package zio_learning

import zio.clock.{ nanoTime, Clock }
import zio.test.Assertion._
import zio.test.{ suite, _ }

object TestEffectsSuiteTest extends DefaultRunnableSpec {
  val suite1: Spec[Clock, TestFailure[Nothing], TestSuccess] = suite("suite1")(
    testM("s1.t1")(assertM(nanoTime)(isGreaterThanEqualTo(0L))),
    testM("s1.t2")(assertM(nanoTime)(isGreaterThanEqualTo(0L)))
  )

  val suite2: Spec[Clock, TestFailure[Nothing], TestSuccess] = suite("suite2")(
    testM("s2.t1")(assertM(nanoTime)(isGreaterThanEqualTo(0L))),
    testM("s2.t2")(assertM(nanoTime)(isGreaterThanEqualTo(0L))),
    testM("s2.t3")(assertM(nanoTime)(isGreaterThanEqualTo(0L)))
  )

  val suite3: Spec[Clock, TestFailure[Nothing], TestSuccess] = suite("suite3")(
    testM("s3.t1")(assertM(nanoTime)(isGreaterThanEqualTo(0L)))
  )

  def spec = suite("All tests")(suite1, suite2, suite3)
}
