package bank

import bank.MyBIOImpl._

trait MyBIOTypeclass[F[+_, +_]] {
  def flatMap[A, B, C](f: B => F[A, C])(implicit interpreter: F[A, B]): F[A, C]
  def map[A, B, C](f: B => C)(implicit interpreter: F[A, B]): F[A, C]
  def listTraverse[A, B, C, D](list: List[(A, B)])(func: (A, B) => F[C, D]): F[List[C], List[D]]
}

object MyBIOTypeclassDsl {
  implicit def myBioDsl: MyBIOTypeclass[MyBIO] = new MyBIOTypeclass[MyBIO] {
    override def flatMap[A, B, C](f: B => MyBIO[A, C])(implicit interpreter: MyBIO[A, B]): MyBIO[A, C] =
      interpreter.flatMap(f)
    override def map[A, B, C](f: B => C)(implicit interpreter: MyBIO[A, B]): MyBIO[A, C] =
      interpreter.map(f)
    override def listTraverse[A, B, C, D](list: List[(A, B)])(func: (A, B) => MyBIO[C, D]): MyBIO[List[C], List[D]] =
      MyBIO.listTraverse(list)(func)
  }
}
