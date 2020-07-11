package bank

import bank.BankHelper._
import bank.BankService._
import cats.Id

object BankApp {

  class BankApplication extends UserService with AccountService with IdGenerator {
    override def addUser(name: String): Boolean = BankMonad.addUser[Id](name)
    override def getUserIdByName(name: String): Either[UserError, UserId] = BankMonad.getUserIdByName[Id](name)
    override def getAllUsersIds: Either[UserError, List[UserId]] = BankMonad.getAllUsersIds[Id]
    override def getAllUsersNames: Either[UserError, List[String]] = BankMonad.getAllUsersNames[Id]

    override def createAccount(userId: UserId): Either[AccountingError, AccountId] = BankMonad.createAccount[Id](userId)
    override def getAccountIdByUser(userId: UserId): Either[AccountingError, AccountId] = BankMonad.getAccountIdByUser[Id](userId)
    override def balance(userId: UserId): Either[AccountingError, Balance] = BankMonad.balance[Id](userId)
    override def put(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance] = BankMonad.put[Id](userId, amount)
    override def charge(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance] = BankMonad.charge[Id](userId, amount)

    def balance(name: String): Either[Error, Balance] = BankMonad.balance[Id](name)
    def put(name: String, amount: BigDecimal): Either[Error, Balance] = BankMonad.put[Id](name, amount)
    def charge(name: String, amount: BigDecimal): Either[Error, Balance] = BankMonad.charge[Id](name, amount)

    def chargeAll(users: List[(String, Balance)]): Either[List[Error], List[Balance]] = {
      val (lefts, rights) = users.map(user => charge(user._1, user._2)).partition(_.isLeft)
      if (lefts.nonEmpty) {
        Left(lefts.map(_.left.getOrElse(new Error())))
      } else {
        Right(rights.map(_.getOrElse(0.0)))
      }
    }

  }

}
