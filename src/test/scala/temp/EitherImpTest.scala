package temp

import org.scalatest.flatspec.AnyFlatSpec
import temp.Helpers._

class EitherImpTest extends AnyFlatSpec {

  "temp.EitherImp" should "returns isRight === true and isLeft === false if it is temp.RightImp" in {
    val either: EitherImp[String, Int] = RightImp(1)
    assert(either.isLeft === false)
    assert(either.isRight === true)
  }

  "temp.EitherImp" should "returns isRight === false and isLeft === true if it is temp.LeftImp" in {
    val either: EitherImp[String, Int] = LeftImp("Test")
    assert(either.isLeft === true)
    assert(either.isRight === false)
  }

  "temp.RightImp.get" should "returns right value" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(rightImp.right.get === 12)
    assertThrows[NoSuchElementException] {
      leftImp.right.get
    }
  }

  "temp.LeftImp.get" should "returns left value" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.get === "Test")
    assertThrows[NoSuchElementException] {
      rightImp.left.get
    }
  }

  "temp.LeftImp.flatMap" should "binds the given function across `Left`" in {
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.flatMap(_ => LeftImp("scala")).left.get === "scala")
    assert(leftImp.leftFlatMap(_ => LeftImp("scala")).left.get === "scala")
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.left.flatMap(_ => LeftImp("scala")).right.get === 12)
    assert(rightImp.leftFlatMap(_ => LeftImp("scala")).right.get === 12)
  }

  "temp.LeftImp.map" should "maps the function argument through `Left`." in {
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.left.map(_ + " scala").left.get === "Test scala")
    assert(leftImp.leftMap(_ + " scala").left.get === "Test scala")
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.left.map(_ + " scala").right.get === 12)
    assert(rightImp.leftMap(_ + " scala").right.get === 12)
  }

  "temp.RightImp.flatMap" should "binds the given function across `Left`" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.flatMap(_ => RightImp("scala")).right.get === "scala")
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.flatMap(_ => RightImp("scala")).left.get === "Test")
  }

  "temp.RightImp.map" should " applied if this is a `Right`" in {
    val rightImp: EitherImp[String, Int] = RightImp(12)
    assert(rightImp.map(_ => "scala").right.get === "scala")
    val leftImp: EitherImp[String, Int] = LeftImp("Test")
    assert(leftImp.map(_ => "scala").left.get === "Test")
  }

  "biTraverse" should " return Either[List[L], List[R]] for Left list" in {
    val seq: Seq[EitherImp[List[Int], String]] = List(LeftImp(List(0, 1, 2, 3, 4, 5)), LeftImp(List(0, 1, 2, 3, 4)),
      LeftImp(List(0, 1, 2, 3)), LeftImp(List(0, 1, 2)), LeftImp(List(0, 1)), LeftImp(List(0)))
    val leftTraverse = seq.biTraverse
    assert(leftTraverse.isLeft === true)
    assert(leftTraverse.isRight === false)
    assert(leftTraverse.left.get === List(0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 2, 0, 1, 0))
  }

  "biTraverse" should " return Either[List[L], List[R]] for Left/Right list" in {
    val seq: Seq[EitherImp[List[Int], String]] = List(RightImp("Test0"), LeftImp(List(0, 1, 2, 3, 4, 5)),
      LeftImp(List(0, 1, 2, 3, 4)), LeftImp(List(0, 1, 2, 3)), RightImp("Test1"), LeftImp(List(0, 1, 2)),
      RightImp("Test2"), LeftImp(List(0, 1)), LeftImp(List(0)), RightImp("Test3"))
    val leftTraverse = seq.biTraverse
    assert(leftTraverse.isLeft === true)
    assert(leftTraverse.isRight === false)
    assert(leftTraverse.left.get === List(0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 2, 0, 1, 0))
  }

  "biTraverse" should " return Either[List[L], List[R]] for Right list" in {
    val seq: Seq[EitherImp[List[Int], String]] = List(RightImp("Test0"), RightImp("Test1"), RightImp("Test2"),
      RightImp("Test3"))
    val leftTraverse = seq.biTraverse
    assert(leftTraverse.isLeft === false)
    assert(leftTraverse.isRight === true)
    assert(leftTraverse.right.get === List("Test0", "Test1", "Test2", "Test3"))
  }

  "biTraverseFlat" should " return Either[List[L], List[R]] for Left list" in {
    val seq: Seq[EitherImp[List[Int], List[String]]] = List(LeftImp(List(0, 1, 2, 3, 4, 5)), LeftImp(List(0, 1, 2, 3, 4)),
      LeftImp(List(0, 1, 2, 3)), LeftImp(List(0, 1, 2)), LeftImp(List(0, 1)), LeftImp(List(0)))
    val leftTraverse = seq.biTraverseFlat
    assert(leftTraverse.isLeft === true)
    assert(leftTraverse.isRight === false)
    assert(leftTraverse.left.get === List(0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 2, 0, 1, 0))
  }

  "biTraverseFlat" should " return Either[List[L], List[R]] for Left/Right list" in {
    val seq: Seq[EitherImp[List[Int], List[String]]] = List(RightImp(List("Test0", "Test1", "Test2")),
      LeftImp(List(0, 1, 2, 3, 4, 5)), LeftImp(List(0, 1, 2, 3, 4)), LeftImp(List(0, 1, 2, 3)),
      RightImp(List("Test1", "Test2", "Test3")), LeftImp(List(0, 1, 2)),
      RightImp(List("Test2", "Test3", "Test4")), LeftImp(List(0, 1)), LeftImp(List(0)),
      RightImp(List("Test5")))
    val leftTraverse = seq.biTraverseFlat
    assert(leftTraverse.isLeft === true)
    assert(leftTraverse.isRight === false)
    assert(leftTraverse.left.get === List(0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 2, 0, 1, 0))
  }

  "biTraverseFlat" should " return Either[List[L], List[R]] for Right list" in {
    val seq: Seq[EitherImp[List[Int], List[String]]] = List(RightImp(List("Test0", "Test1", "Test2")),
      RightImp(List("Test1", "Test2", "Test3")), RightImp(List("Test2", "Test3", "Test4")), RightImp(List("Test5")))
    val leftTraverse = seq.biTraverseFlat
    assert(leftTraverse.isLeft === false)
    assert(leftTraverse.isRight === true)
    assert(leftTraverse.right.get === List("Test0", "Test1", "Test2", "Test1", "Test2", "Test3", "Test2", "Test3", "Test4", "Test5"))
  }

}
