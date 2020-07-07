import org.scalatest.flatspec.AnyFlatSpec

class EitherImpTest extends AnyFlatSpec {

  "EitherImp.isLeft.isRight" should "returns isRight === true and isLeft === false if it is RightImp" in {
    val either: EitherImp[String, Int] = RightImp(1)
    assert(either.isLeft === false)
    assert(either.isRight === true)
  }

  "EitherImp.isLeft.isRight" should "returns isRight === false and isLeft === true if it is LeftImp" in {
    val either: EitherImp[String, Int] = LeftImp("Test")
    assert(either.isLeft === true)
    assert(either.isRight === false)
  }

}
