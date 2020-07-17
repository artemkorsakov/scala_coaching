package bank

import bank.BankService._

abstract case class BankServiceImpl[F[+_, +_] : MyBifunctor]() extends BankService[F] {

}


