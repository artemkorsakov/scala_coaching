package bank

import bank.BankService._
import bank.MyBIOImpl.MyBIO

import scala.collection.mutable

object BankServiceDsl extends IdGenerator {
  implicit val myBIODsl: BankService[MyBIO] = new BankService[MyBIO] {
    private val users: mutable.HashMap[String, UserId] = mutable.HashMap.empty[String, UserId]
    private val accounts: mutable.HashMap[UserId, (AccountId, Balance)] =
      mutable.HashMap.empty[UserId, (AccountId, Balance)]

    override def addUser(name: String): Boolean =
      if (users.contains(name)) false
      else {
        users += (name -> newId)
        true
      }

    override def getUserIdByName(name: String): MyBIO[UserError, UserId] =
      if (users.contains(name)) MyBIO(users(name)) else MyBIO(Left(NameNotFound(name)))

    override def getAllUsersIds: MyBIO[UserError, List[UserId]] =
      if (users.isEmpty) MyBIO(Left(new UserListIsEmpty)) else MyBIO(users.values.toList)

    override def getAllUsersNames: MyBIO[UserError, List[String]] =
      if (users.isEmpty) MyBIO(Left(new UserListIsEmpty)) else MyBIO(users.keySet.toList)

    override def createAccount(userId: UserId): MyBIO[AccountingError, AccountId] =
      if (accounts.contains(userId)) {
        MyBIO(Left(UserAlreadyHasAccount(userId)))
      } else if (!users.values.toList.contains(userId)) {
        MyBIO(Left(UserNotFound(userId)))
      } else {
        val id = newId
        accounts += (userId -> (id, 0.0))
        MyBIO(id)
      }

    override def getAccountIdByUser(userId: UserId): MyBIO[AccountingError, AccountId] =
      if (accounts.contains(userId)) MyBIO(accounts(userId)._1) else MyBIO(Left(UserNotFound(userId)))

    override def balance(userId: UserId): MyBIO[AccountingError, Balance] =
      if (accounts.contains(userId)) MyBIO(accounts(userId)._2) else MyBIO(Left(UserNotFound(userId)))

    override def put(userId: UserId, amount: BigDecimal): MyBIO[AccountingError, Balance] =
      if (amount < 0) {
        MyBIO(Left(AmountLessThanZero(amount)))
      } else if (!accounts.contains(userId)) {
        MyBIO(Left(UserNotFound(userId)))
      } else {
        changeBalance(userId, amount)
      }

    override def charge(userId: UserId, amount: BigDecimal): MyBIO[AccountingError, Balance] =
      if (!accounts.contains(userId)) {
        MyBIO(Left(UserNotFound(userId)))
      } else {
        val balance: Balance = accounts(userId)._2
        if (balance < amount) {
          MyBIO(Left(BalanceTooLow(balance)))
        } else {
          changeBalance(userId, -amount)
        }
      }

    private def changeBalance(userId: UserId, amount: BigDecimal): MyBIO[AccountingError, Balance] = {
      val newAmount: Balance = accounts(userId)._2 + amount
      accounts(userId) = (accounts(userId)._1, newAmount)
      balance(userId)
    }
  }

}
