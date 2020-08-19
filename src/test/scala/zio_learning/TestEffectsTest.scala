package zio_learning

import zio.clock.nanoTime
import zio.{ console, random }
import zio.test.Assertion.{ equalTo, isRight, isSome, _ }
import zio.test.environment.{ TestConsole, TestRandom }
import zio.test.{ Assertion, assert, _ }

object TestEffectsTest extends DefaultRunnableSpec {
  final case class Address(country: String, city: String)
  final case class User(name: String, age: Int, address: Address)

  val assertionForString: Assertion[String] = Assertion.containsString("Foo") && Assertion.endsWithString("Bar")

  val clockSuite = suite("clock")(
    testM("time is non-zero") {
      assertM(nanoTime)(isGreaterThanEqualTo(0L))
    }
  )

  val paymentProviderABCSuite = suite("ABC payment provider tests") {
    test("Your test") {
      assert("Your value")(Assertion.isNonEmptyString)
    }
  }

  val paymentProviderXYZSuite = suite("XYZ payment provider tests") {
    test("Your other test") {
      assert("Your other value")(Assertion.isNonEmptyString)
    }
  }

  val tempSuite = suite("Temp tests")(
    test("Temp test") {
      assert("FooFooBar")(assertionForString)
    },
    test("Check assertions") {
      assert(Right(Some(2)))(isRight(isSome(equalTo(2))))
    },
    test("Rich checking") {
      assert(
        User("Jonny", 26, Address("Denmark", "Copenhagen"))
      )(
        hasField("age", (u: User) => u.age, isGreaterThanEqualTo(18)) &&
        hasField("country", (u: User) => u.address.country, not(equalTo("USA")))
      )
    },
    testM("Use setSeed to generate stable values") {
      for {
        _  <- TestRandom.setSeed(27)
        r1 <- random.nextLong
        r2 <- random.nextLong
        r3 <- random.nextLong
      } yield assert(List(r1, r2, r3))(
        equalTo(
          List[Long](
            -4947896108136290151L,
            -5264020926839611059L,
            -9135922664019402287L
          )
        )
      )
    },
    testM("One can provide its own list of ints") {
      for {
        _  <- TestRandom.feedInts(1, 9, 2, 8, 3, 7, 4, 6, 5)
        r1 <- random.nextInt
        r2 <- random.nextInt
        r3 <- random.nextInt
        r4 <- random.nextInt
        r5 <- random.nextInt
        r6 <- random.nextInt
        r7 <- random.nextInt
        r8 <- random.nextInt
        r9 <- random.nextInt
      } yield assert(
        List(1, 9, 2, 8, 3, 7, 4, 6, 5)
      )(equalTo(List(r1, r2, r3, r4, r5, r6, r7, r8, r9)))
    }
  )

  val consoleSuite = suite("ConsoleTest")(
    testM("One can test output of console") {
      for {
        _              <- TestConsole.feedLines("Jimmy", "37")
        _              <- console.putStrLn("What is your name?")
        name           <- console.getStrLn
        _              <- console.putStrLn("What is your age?")
        age            <- console.getStrLn.map(_.toInt)
        questionVector <- TestConsole.output
        q1 = questionVector(0)
        q2 = questionVector(1)
      } yield {
        assert(name)(equalTo("Jimmy")) &&
        assert(age)(equalTo(37)) &&
        assert(q1)(equalTo("What is your name?\n")) &&
        assert(q2)(equalTo("What is your age?\n"))
      }
    }
  )

  val allPaymentProvidersTests =
    suite("All payment providers tests")(
      consoleSuite,
      clockSuite,
      paymentProviderABCSuite,
      paymentProviderXYZSuite,
      tempSuite
    )

  def spec = allPaymentProvidersTests

}
