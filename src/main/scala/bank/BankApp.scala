package bank

import bank.BankService._
import bank.BankServiceDsl._

object BankApp {

  class BankApplication extends BankService[My] {
    override def addUser(name: String): Boolean = BankService.addUser[My](name)

    override def getUserIdByName(name: String): My[UserError, UserId] = BankService.getUserIdByName[My](name)

    override def getAllUsersIds: My[UserError, List[UserId]] = BankService.getAllUsersIds[My]

    override def getAllUsersNames: My[UserError, List[String]] = BankService.getAllUsersNames[My]

    override def createAccount(userId: UserId): My[AccountingError, AccountId] = BankService.createAccount[My](userId)

    override def getAccountIdByUser(userId: UserId): My[AccountingError, AccountId] = BankService.getAccountIdByUser[My](userId)

    override def balance(userId: UserId): My[AccountingError, Balance] = BankService.balance[My](userId)

    override def put(userId: UserId, amount: BigDecimal): My[AccountingError, Balance] = BankService.put[My](userId, amount)

    override def charge(userId: UserId, amount: BigDecimal): My[AccountingError, Balance] = BankService.charge[My](userId, amount)

    def balance(name: String): Either[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- balance(id)
      } yield {
        newBalance
      }
    }

    def put(name: String, amount: BigDecimal): Either[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- put(id, amount)
      } yield {
        newBalance
      }
    }

    def charge(name: String, amount: BigDecimal): Either[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- charge(id, amount)
      } yield {
        newBalance
      }
    }

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
