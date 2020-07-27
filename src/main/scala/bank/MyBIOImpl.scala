package bank

import bank.BankServiceDsl.MyIO
import cats.effect.IO
import cats.effect.unsafe.implicits.global

object MyBIOImpl {
  case class Wrapper[+A, +B](value: MyIO[A, B])

  type MyBIO[+A, +B] = Wrapper[A, B]

  object MyBIO {
    def apply[A, B](value: Either[A, B]): MyBIO[A, B] = new MyBIO(IO(value))
    def apply[B](value: B): MyBIO[Nothing, B]         = new MyBIO(IO(Right(value)))
  }

  implicit class Syntax[A, B](myBio: MyBIO[A, B]) {
    def flatMap[C](f: B => MyBIO[A, C]): MyBIO[A, C] =
      myBio.value.unsafeRunSync() match {
        case Right(value) => f(value)
        case Left(value)  => new MyBIO(IO(Left(value)))
      }

    final def map[C](f: B => C): MyBIO[A, C] =
      myBio.value.unsafeRunSync() match {
        case Right(value) => MyBIO(f(value))
        case Left(value)  => new MyBIO(IO(Left(value)))
      }
  }

}
