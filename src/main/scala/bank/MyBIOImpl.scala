package bank

import cats.effect._

object MyBIOImpl {
  case class Wrapper[+A, +B](value: IO[Either[A, B]])

  type MyBIO[+A, +B] = Wrapper[A, B]

  object MyBIO {
    def apply[A, B](value: Either[A, B]): MyBIO[A, B] = new MyBIO(IO(value))
    def apply[B](value: B): MyBIO[Nothing, B]         = new MyBIO(IO(Right(value)))
  }

  implicit class Syntax[A, B](mybio: MyBIO[A, B]) {
    def flatMap[C](f: B => MyBIO[A, C]): MyBIO[A, C] =
      for {
        b <- mybio
        p <- f(b)
      } yield p

    final def map[C](f: B => C): MyBIO[A, C] =
      for {
        b <- mybio
      } yield f(b)
  }

  /*
val p = for {
  a <- MyBIO(1)
  b <- MyBIO(2)
} yield {
  a + b
}

println(p.value.unsafeRunSync())
 */
}
