package bank

import bank.BankService._
import cats.effect._

import scala.collection.mutable

object BankServiceDsl extends IdGenerator {
  type MyIO[+A, +B] = IO[Either[A, B]]

  implicit val myIODsl: BankService[MyIO] = new BankService[MyIO] {
    private val users: mutable.HashMap[String, UserId] = mutable.HashMap.empty[String, UserId]
    private val accounts: mutable.HashMap[UserId, (AccountId, Balance)] =
      mutable.HashMap.empty[UserId, (AccountId, Balance)]

    override def addUser(name: String): Boolean =
      if (users.contains(name)) false
      else {
        users += (name -> newId)
        true
      }

    override def getUserIdByName(name: String): MyIO[UserError, UserId] =
      if (users.contains(name)) IO.pure(Right(users(name))) else IO.pure(Left(NameNotFound(name)))

    override def getAllUsersIds: MyIO[UserError, List[UserId]] =
      if (users.isEmpty) IO.pure(Left(new UserListIsEmpty)) else IO.pure(Right(users.values.toList))

    override def getAllUsersNames: MyIO[UserError, List[String]] =
      if (users.isEmpty) IO.pure(Left(new UserListIsEmpty)) else IO.pure(Right(users.keySet.toList))

    override def createAccount(userId: UserId): MyIO[AccountingError, AccountId] =
      if (accounts.contains(userId)) {
        IO.pure(Left(UserAlreadyHasAccount(userId)))
      } else if (!users.values.toList.contains(userId)) {
        IO.pure(Left(UserNotFound(userId)))
      } else {
        val id = newId
        accounts += (userId -> (id, 0.0))
        IO.pure(Right(id))
      }

    override def getAccountIdByUser(userId: UserId): MyIO[AccountingError, AccountId] =
      if (accounts.contains(userId)) IO.pure(Right(accounts(userId)._1)) else IO.pure(Left(UserNotFound(userId)))

    override def balance(userId: UserId): MyIO[AccountingError, Balance] =
      if (accounts.contains(userId)) IO.pure(Right(accounts(userId)._2)) else IO.pure(Left(UserNotFound(userId)))

    override def put(userId: UserId, amount: BigDecimal): MyIO[AccountingError, Balance] =
      if (amount < 0) {
        IO.pure(Left(AmountLessThanZero(amount)))
      } else if (!accounts.contains(userId)) {
        IO.pure(Left(UserNotFound(userId)))
      } else {
        changeBalance(userId, amount)
      }

    override def charge(userId: UserId, amount: BigDecimal): MyIO[AccountingError, Balance] =
      if (!accounts.contains(userId)) {
        IO.pure(Left(UserNotFound(userId)))
      } else {
        val balance: Balance = accounts(userId)._2
        if (balance < amount) {
          IO.pure(Left(BalanceTooLow(balance)))
        } else {
          changeBalance(userId, -amount)
        }
      }

    private def changeBalance(userId: UserId, amount: BigDecimal): MyIO[AccountingError, Balance] = {
      val newAmount: Balance = accounts(userId)._2 + amount
      accounts(userId) = (accounts(userId)._1, newAmount)
      balance(userId)
    }
  }

}
