package bank

import bank.BankService.{ AccountId, Balance, BankService, UserId }
import bank.BankServiceDsl.myBIODsl
import bank.MyBIOImpl.MyBIO

object MyBIOTypeclassDsl {
  implicit val myBIOTypeclassDsl: MyBIOTypeclass[MyBIO] = new MyBIOTypeclass[MyBIO] {
    def addUser(name: String): Boolean =
      BankService.addUser(name)
    def getUserIdByName(name: String): MyBIO[Error, UserId] =
      BankService.getUserIdByName(name)
    def getAllUsersIds: MyBIO[Error, List[UserId]] =
      BankService.getAllUsersIds
    def getAllUsersNames: MyBIO[Error, List[String]] =
      BankService.getAllUsersNames

    def createAccount(userId: UserId): MyBIO[Error, AccountId] =
      BankService.createAccount(userId)
    def getAccountIdByUser(userId: UserId): MyBIO[Error, AccountId] =
      BankService.getAccountIdByUser(userId)
    def balance(userId: UserId): MyBIO[Error, Balance] =
      BankService.balance(userId)
    def put(userId: UserId, amount: BigDecimal): MyBIO[Error, Balance] =
      BankService.put(userId, amount)
    def charge(userId: UserId, amount: BigDecimal): MyBIO[Error, Balance] =
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

    def chargeAll(users: List[(String, Balance)]): MyBIO[List[Error], List[Balance]] =
      MyBIO.listTraverse(users)(charge)
  }
}
