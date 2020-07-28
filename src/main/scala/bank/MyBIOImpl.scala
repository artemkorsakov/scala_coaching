package bank

import cats.effect.IO

object MyBIOImpl {
  case class Wrapper[+A, +B](value: IO[Either[A, B]])

  type MyBIO[+A, +B] = Wrapper[A, B]

  object MyBIO {
    def apply[A, B](value: Either[A, B]): MyBIO[A, B] = new MyBIO(IO(value))
    def apply[B](value: B): MyBIO[Nothing, B]         = new MyBIO(IO(Right(value)))
  }

  implicit class Syntax[A, B](myBio: MyBIO[A, B]) {
    def flatMap[C](f: B => MyBIO[A, C]): MyBIO[A, C] =
      new MyBIO(myBio.value.flatMap {
        case Right(value) => f(value).value
        case Left(value)  => IO(Left(value))
      })

    final def map[C](f: B => C): MyBIO[A, C] =
      new MyBIO(myBio.value.flatMap {
        case Right(value) => IO(Right(f(value)))
        case Left(value)  => IO(Left(value))
      })
  }

}
