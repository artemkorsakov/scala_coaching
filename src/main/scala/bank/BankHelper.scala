package bank

import bank.BankMonad.BiMonad
import bank.BankService._
import cats.Id

import scala.collection.mutable

object BankHelper extends IdGenerator {
  implicit val simpleDsl: BiMonad[Id] = new BiMonad[Id] {
    private val users: mutable.HashMap[String, UserId] = mutable.HashMap.empty[String, UserId]
    private val accounts: mutable.HashMap[UserId, (AccountId, Balance)] = mutable.HashMap.empty[UserId, (AccountId, Balance)]

    override def addUser(name: String): Id[Boolean] = if (users.contains(name)) false else {
      users += (name -> newId)
      true
    }

    override def getAllUsersIds: Id[Either[UserError, List[UserId]]] =
      if (users.isEmpty) Left(new UserListIsEmpty) else Right(users.values.toList)

    override def getAllUsersNames: Id[Either[UserError, List[String]]] =
      if (users.isEmpty) Left(new UserListIsEmpty) else Right(users.keySet.toList)

    override def getUserIdByName(name: String): Id[Either[UserError, UserId]] =
      if (users.contains(name)) Right(users(name)) else Left(NameNotFound(name))


    override def createAccount(userId: UserId): Id[Either[AccountingError, AccountId]] =
      if (accounts.contains(userId)) {
        Left(UserAlreadyHasAccount(userId))
      } else if (!getAllUsersIds.getOrElse(List.empty[UserId]).contains(userId)) {
        Left(UserNotFound(userId))
      } else {
        val id = newId
        accounts += (userId -> (id, 0.0))
        Right(id)
      }

    override def getAccountIdByUser(userId: UserId): Id[Either[AccountingError, AccountId]] =
      if (accounts.contains(userId)) Right(accounts(userId)._1) else Left(UserNotFound(userId))

    override def balance(userId: UserId): Id[Either[AccountingError, Balance]] =
      if (accounts.contains(userId)) Right(accounts(userId)._2) else Left(UserNotFound(userId))

    override def put(userId: UserId, amount: BigDecimal): Id[Either[AccountingError, Balance]] =
      if (amount < 0) {
        Left(AmountLessThanZero(amount))
      } else if (!accounts.contains(userId)) {
        Left(UserNotFound(userId))
      } else {
        changeBalance(userId, amount)
      }

    override def charge(userId: UserId, amount: BigDecimal): Id[Either[AccountingError, Balance]] =
      if (!accounts.contains(userId)) {
        Left(UserNotFound(userId))
      } else {
        val balance: Balance = accounts(userId)._2
        if (balance < amount) {
          Left(BalanceTooLow(balance))
        } else {
          changeBalance(userId, -amount)
        }
      }

    private def changeBalance(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance] = {
      val newAmount: Balance = accounts(userId)._2 + amount
      accounts(userId) = (accounts(userId)._1, newAmount)
      balance(userId)
    }

  }
}
