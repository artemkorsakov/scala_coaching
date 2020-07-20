package bank

case class MyBifunctor[F[+_, +_]]() {
  def map[A, B, C](a: F[A, B])(f: B => C): F[A, C] = ???
  def flatMap[A, B, C](a: F[A, B])(f: B => F[A, C]): F[A, C] = ???
}

object MyBifunctor {

  /*
  implicit def MyBifunctorIOEither[A, B]: MyBifunctor[cats.effect.IO[Either[A, B]]] = new MyBifunctor[cats.effect.IO[Either[A, B]] {


  }

   */
}
