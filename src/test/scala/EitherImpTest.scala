import org.scalatest.flatspec.AnyFlatSpec

class EitherImpTest extends AnyFlatSpec {

  "EitherImp" should "returns isRight === true and isLeft === false if it is RightImp" in {
    val either: EitherImp[String, Int] = RightImp(1)
    assert(either.isLeft === false)
    assert(either.isRight === true)
  }

  "EitherImp" should "returns isRight === false and isLeft === true if it is LeftImp" in {
    val either: EitherImp[String, Int] = LeftImp("Test")
    assert(either.isLeft === true)
    assert(either.isRight === false)
  }

  "RightImp.get" should "returns right value" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(rightImp.right.get === 12)
    assertThrows[NoSuchElementException] {
      leftImp.right.get
    }
  }

  "LeftImp.get" should "returns left value" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.get === "Test")
    assertThrows[NoSuchElementException] {
      rightImp.left.get
    }
  }

  "LeftImp.flatMap" should "binds the given function across `Left`" in {
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.flatMap(_ => LeftImp("scala")).left.get === "scala")
    assert(leftImp.leftFlatMap(_ => LeftImp("scala")).left.get === "scala")
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.left.flatMap(_ => LeftImp("scala")).right.get === 12)
    assert(rightImp.leftFlatMap(_ => LeftImp("scala")).right.get === 12)
  }

  "LeftImp.map" should "maps the function argument through `Left`." in {
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.map(_ + " scala").left.get === "Test scala")
    assert(leftImp.leftMap(_ + " scala").left.get === "Test scala")
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.left.map(_ + " scala").right.get === 12)
    assert(rightImp.leftMap(_ + " scala").right.get === 12)
  }

  "RightImp.flatMap" should "binds the given function across `Left`" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.flatMap(_ => RightImp("scala")).right.get === "scala")
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.flatMap(_ => RightImp("scala")).left.get === "Test")
  }

  "RightImp.map" should " applied if this is a `Right`" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.map(_ => "scala").right.get === "scala")
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.map(_ => "scala").left.get === "Test")
  }

}
