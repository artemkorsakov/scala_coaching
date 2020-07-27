package bank

import bank.BankService._
import bank.BankServiceDsl._
import bank.MyBIOImpl.MyBIO
import cats.effect.unsafe.implicits.global

object BankApp {

  class BankApplication extends BankService[MyBIO] {
    override def addUser(name: String): Boolean =
      BankService.addUser(name)
    override def getUserIdByName(name: String): MyBIO[Error, UserId] =
      BankService.getUserIdByName(name)
    override def getAllUsersIds: MyBIO[Error, List[UserId]] =
      BankService.getAllUsersIds
    override def getAllUsersNames: MyBIO[Error, List[String]] =
      BankService.getAllUsersNames
    override def createAccount(userId: UserId): MyBIO[Error, AccountId] =
      BankService.createAccount(userId)
    override def getAccountIdByUser(userId: UserId): MyBIO[Error, AccountId] =
      BankService.getAccountIdByUser(userId)
    override def balance(userId: UserId): MyBIO[Error, Balance] =
      BankService.balance(userId)
    override def put(userId: UserId, amount: BigDecimal): MyBIO[Error, Balance] =
      BankService.put(userId, amount)
    override def charge(userId: UserId, amount: BigDecimal): MyBIO[Error, Balance] =
      BankService.charge(userId, amount)

    def balance(name: String): MyBIO[Error, Balance] =
      for {
        id         <- getUserIdByName(name)
        newBalance <- balance(id)
      } yield {
        newBalance
      }

    def put(name: String, amount: BigDecimal): MyBIO[Error, Balance] =
      for {
        id         <- getUserIdByName(name)
        newBalance <- put(id, amount)
      } yield {
        newBalance
      }

    def charge(name: String, amount: BigDecimal): MyBIO[Error, Balance] =
      for {
        id         <- getUserIdByName(name)
        newBalance <- charge(id, amount)
      } yield {
        newBalance
      }

    def chargeAll(users: List[(String, Balance)]): MyBIO[List[Error], List[Balance]] = {
      val (lefts, rights) = users.map(user => charge(user._1, user._2)).partition(_.value.unsafeRunSync().isLeft)
      if (lefts.nonEmpty) {
        MyBIO(Left(lefts.map(_.value.unsafeRunSync().left.getOrElse(new Error()))))
      } else {
        MyBIO(rights.map(bio => bio.value.unsafeRunSync().getOrElse(0: Balance)))
      }
    }
  }

}
