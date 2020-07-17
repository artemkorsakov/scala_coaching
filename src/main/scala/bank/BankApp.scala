package bank

import bank.BankService._
import bank.BankServiceDsl._
import bank.BankServiceOps._

object BankApp {

  class BankApplication extends BankService[My] {
    override def addUser(name: String): Boolean = name.addUser
    override def getUserIdByName(name: String): My[UserError, UserId] = name.getUserIdByName
    override def getAllUsersIds: My[UserError, List[UserId]] = "".getAllUsersIds
    override def getAllUsersNames: My[UserError, List[String]] = "".getAllUsersNames
    override def createAccount(userId: UserId): My[AccountingError, AccountId] = userId.createAccount
    override def getAccountIdByUser(userId: UserId): My[AccountingError, AccountId] = userId.getAccountIdByUser
    override def balance(userId: UserId): My[AccountingError, Balance] = userId.balance
    override def put(userId: UserId, amount: BigDecimal): My[AccountingError, Balance] = userId.put(amount)
    override def charge(userId: UserId, amount: BigDecimal): My[AccountingError, Balance] = userId.charge(amount)

    def balance(name: String): My[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- balance(id)
      } yield {
        newBalance
      }
    }

    def put(name: String, amount: BigDecimal): My[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- put(id, amount)
      } yield {
        newBalance
      }
    }

    def charge(name: String, amount: BigDecimal): My[Error, Balance] = {
      for {
        id <- getUserIdByName(name)
        newBalance <- charge(id, amount)
      } yield {
        newBalance
      }
    }

    def chargeAll(users: List[(String, Balance)]): My[List[Error], List[Balance]] = {
      val (lefts, rights) = users.map(user => charge(user._1, user._2)).partition(_.isLeft)
      if (lefts.nonEmpty) {
        Left(lefts.map(_.left.getOrElse(new Error())))
      } else {
        Right(rights.map(_.getOrElse(0.0)))
      }
    }

  }

}
