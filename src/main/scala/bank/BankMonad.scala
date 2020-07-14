package bank

import bank.BankService._
import cats.effect.IO

object BankMonad extends IdGenerator {
  type My[+A, +B] = cats.effect.IO[Either[A, B]]

  trait BankServiceImpl[F[+_, +_] : MyBifunctor] extends BankService[F] {

    /*
    def balance(name: String): F[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- balance(id)
      } yield {
        newBalance
      }
    }

     */
  }

  trait MyBifunctor[F[+_, +_]] {
    def map[E, A, B](a: F[E, A])(f: A => B): F[E, B]
    def flatMap[E, A, B](a: F[E, A])(f: A => F[E, B]): F[E, B]
  }

  object MyBifunctor {
    implicit def MyBifunctorIOEither[A, B] = new MyBifunctor[cats.effect.IO[Either[A, B]]] {
      // здесь реализации методов из trait MyBifunctor
      override def map[E, A, B](a: IO[E, A])(f: A => B): IO[E, B] = ???

      override def flatMap[E, A, B](a: IO[E, A])(f: A => IO[E, B]): IO[E, B] = ???
    }
  }

}
