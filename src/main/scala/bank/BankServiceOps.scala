package bank

import bank.BankService._

object BankServiceOps {
  implicit class BankServiceUserServiceOps[F[+_, +_]](value: String) {
    def addUser(implicit bs: BankService[F]): Boolean =
      bs.addUser(value)
    def getUserIdByName(implicit bs: BankService[F]): F[UserError, UserId] =
      bs.getUserIdByName(value)
    def getAllUsersIds(implicit interpreter: BankService[F]): F[UserError, List[UserId]] =
      interpreter.getAllUsersIds
    def getAllUsersNames(implicit interpreter: BankService[F]): F[UserError, List[String]] =
      interpreter.getAllUsersNames
  }

  implicit class BankServiceAccountServiceOps[F[+_, +_]](userId: UserId) {
    def createAccount(implicit interpreter: BankService[F]): F[AccountingError, AccountId] =
      interpreter.createAccount(userId)
    def getAccountIdByUser(implicit interpreter: BankService[F]): F[AccountingError, AccountId] =
      interpreter.getAccountIdByUser(userId)
    def balance(implicit interpreter: BankService[F]): F[AccountingError, Balance] =
      interpreter.balance(userId)
    def put(amount: BigDecimal)(implicit interpreter: BankService[F]): F[AccountingError, Balance] =
      interpreter.put(userId, amount)
    def charge(amount: BigDecimal)(implicit interpreter: BankService[F]): F[AccountingError, Balance] =
      interpreter.charge(userId, amount)
  }

}
