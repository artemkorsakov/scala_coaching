package zio_learning

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.{ IO, Task, UIO, ZIO, _ }

class CreatingEffectsTests extends AnyFunSuiteLike with Matchers {
  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  test("test creating Effects From Success Values") {
    val s1 = ZIO.succeed(42)
    runtime.unsafeRun(s1) shouldBe 42

    val s2: Task[Int] = Task.succeed(42)
    runtime.unsafeRun(s2) shouldBe 42

    val now = ZIO.effectTotal(System.currentTimeMillis())
    runtime.unsafeRun(now) > 0 shouldBe true
  }

  test("test creating Effects From Failure Values") {
    val f1 = ZIO.fail("Uh oh!")
    println(f1)

    val f2 = Task.fail(new Exception("Uh oh!"))
    println(f2)
  }

  test("test creating Effects. From Scala Values. Option") {
    val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
    runtime.unsafeRun(zoption) shouldBe 2

    val zoption2: IO[String, Int] = zoption.mapError(_ => "It wasn't there!")

    case class User(userId: String, teamId: String)
    case class Team()

    val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))
  }

  test("test creating Effects. From Scala Values. Either") {
    val zeither = ZIO.fromEither(Right("Success!"))
    runtime.unsafeRun(zeither) shouldBe "Success!"
  }

  test("test creating Effects. From Scala Values. Try") {
    import scala.util.Try

    val ztry = ZIO.fromTry(Try(42 / 0))
  }

  test("test creating Effects. From Scala Values. Function") {
    val zfun: URIO[Int, Int] = ZIO.fromFunction((i: Int) => i * i)
  }

  test("test creating Effects. From Scala Values. Future") {
    import scala.concurrent.Future

    lazy val future = Future.successful("Hello!")

    val zfuture: Task[String] =
      ZIO.fromFuture(implicit ec => future.map(_ => "Goodbye!"))
    runtime.unsafeRun(zfuture) shouldBe "Goodbye!"
  }

  test("test creating Effects. Synchronous Side-Effects") {
    import scala.io.StdIn

    val getStrLn: Task[String]            = ZIO.effect(StdIn.readLine())
    def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))
  }

  test("test creating Effects. Blocking Synchronous Side-Effects") {
    import zio.blocking._

    val sleeping = effectBlocking(Thread.sleep(Long.MaxValue))

    import java.net.ServerSocket

    def accept(l: ServerSocket) =
      effectBlockingCancelable(l.accept())(UIO.effectTotal(l.close()))

    import scala.io.{ Codec, Source }

    def download(url: String) =
      Task.effect {
        Source.fromURL(url)(Codec.UTF8).mkString
      }

    def safeDownload(url: String) =
      blocking(download(url))
  }
}
