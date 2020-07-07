import org.scalatest.flatspec.AnyFlatSpec

class EitherImpTest extends AnyFlatSpec {

  "EitherImp.hello" should "return \"Hello, World!\" text" in {
    assert(EitherImp.hello() === "Hello, World!")
  }

}
