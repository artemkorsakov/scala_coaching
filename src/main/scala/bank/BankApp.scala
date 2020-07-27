package bank

import bank.BankService._
import bank.BankServiceDsl._

object BankApp {

  class BankApplication extends BankService[MyIO] {
    override def addUser(name: String): Boolean =
      BankService.addUser(name)
    override def getUserIdByName(name: String): MyIO[UserError, UserId] =
      BankService.getUserIdByName(name)
    override def getAllUsersIds: MyIO[UserError, List[UserId]] =
      BankService.getAllUsersIds
    override def getAllUsersNames: MyIO[UserError, List[String]] =
      BankService.getAllUsersNames
    override def createAccount(userId: UserId): MyIO[AccountingError, AccountId] =
      BankService.createAccount(userId)
    override def getAccountIdByUser(userId: UserId): MyIO[AccountingError, AccountId] =
      BankService.getAccountIdByUser(userId)
    override def balance(userId: UserId): MyIO[AccountingError, Balance] =
      BankService.balance(userId)
    override def put(userId: UserId, amount: BigDecimal): MyIO[AccountingError, Balance] =
      BankService.put(userId, amount)
    override def charge(userId: UserId, amount: BigDecimal): MyIO[AccountingError, Balance] =
      BankService.charge(userId, amount)
  }

  /*
    def balanceByName(name: String): MyIO[Error, Balance] =
      for {
        id         <- getUserIdByName(name)
        newBalance <- balance(id)
      } yield {
        id + newBalance
      }

  def put(name: String, amount: BigDecimal): My[Error, Balance] =
    for {
      id         <- getUserIdByName(name)
      newBalance <- put(id, amount)
    } yield {
      newBalance
    }

  def charge(name: String, amount: BigDecimal): My[Error, Balance] =
    for {
      id         <- getUserIdByName(name)
      newBalance <- charge(id, amount)
    } yield {
      newBalance
    }

  def chargeAll(users: List[(String, Balance)]): My[List[Error], List[Balance]] = {
    val (lefts, rights) = users.map(user => charge(user._1, user._2)).partition(_.isLeft)
    if (lefts.nonEmpty) {
      Left(lefts.map(_.left.getOrElse(new Error())))
    } else {
      Right(rights.map(_.getOrElse(0.0)))
    }
  }

 */

}
