package bank

import bank.BankService._

abstract case class BankServiceImpl[F[+_, +_] : MyBifunctor]() extends BankService[F] {
  /*
  import MyBifunctor._ // в companion MyBifunctor кладешь синтаксические расширения

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
