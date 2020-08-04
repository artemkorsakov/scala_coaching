package bank

import bank.BankService._

object BankApp {

  class BankApplication[F[+_, +_]: MyBIOTypeclass] extends BankService[F] {
    override def addUser(name: String): Boolean =
      MyBIOTypeclass.addUser(name)
    override def getUserIdByName(name: String): F[Error, UserId] =
      MyBIOTypeclass.getUserIdByName(name)
    override def getAllUsersIds: F[Error, List[UserId]] =
      MyBIOTypeclass.getAllUsersIds
    override def getAllUsersNames: F[Error, List[String]] =
      MyBIOTypeclass.getAllUsersNames
    override def createAccount(userId: UserId): F[Error, AccountId] =
      MyBIOTypeclass.createAccount(userId)
    override def getAccountIdByUser(userId: UserId): F[Error, AccountId] =
      MyBIOTypeclass.getAccountIdByUser(userId)
    override def balance(userId: UserId): F[Error, Balance] =
      MyBIOTypeclass.balance(userId)
    override def put(userId: UserId, amount: BigDecimal): F[Error, Balance] =
      MyBIOTypeclass.put(userId, amount)
    override def charge(userId: UserId, amount: BigDecimal): F[Error, Balance] =
      MyBIOTypeclass.charge(userId, amount)
    def balance(name: String): F[Error, Balance] =
      MyBIOTypeclass.balance(name)
    def put(name: String, amount: BigDecimal): F[Error, Balance] =
      MyBIOTypeclass.put(name, amount)
    def charge(name: String, amount: BigDecimal): F[Error, Balance] =
      MyBIOTypeclass.charge(name, amount)
    def chargeAll(users: List[(String, Balance)]): F[List[Error], List[Balance]] =
      MyBIOTypeclass.chargeAll(users)
  }

}
