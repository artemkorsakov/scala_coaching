package bank

import scala.collection.mutable

object Bank {

  import java.util.UUID

  trait DomainError

  sealed trait UserError extends Error

  case class NameNotFound(name: String) extends UserError

  case class UserListIsEmpty() extends UserError

  sealed trait AccountingError extends Error

  case class UserNotFound(userId: UserId) extends AccountingError

  case class UserAlreadyHasAccount(userId: UserId) extends AccountingError

  case class AccountIdNotFound(accountId: AccountId) extends AccountingError

  case class BalanceTooLow(currentBalance: Balance) extends AccountingError

  case class AmountLessThanZero(amount: BigDecimal) extends AccountingError

  trait IdGenerator {
    def newId: UUID = UUID.randomUUID()
  }

  type UserId = UUID

  trait UserService {
    def addUser(name: String): Boolean

    def getUserIdByName(name: String): Either[UserError, UserId]

    def getAllUsersIds: Either[UserError, List[UserId]]

    def getAllUsersNames: Either[UserError, List[String]]
  }

  type Balance = BigDecimal
  type AccountId = UUID

  trait AccountService {
    def createAccount(userId: UserId): Either[AccountingError, AccountId]

    def getAccountIdByUser(userId: UserId): Either[AccountingError, AccountId]

    def balance(userId: UserId): Either[AccountingError, Balance]

    def put(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance]

    def charge(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance]
  }

  class BankApplication extends UserService with AccountService with IdGenerator {
    private val users: mutable.HashMap[String, UserId] = mutable.HashMap.empty[String, UserId]
    private val accounts: mutable.HashMap[UserId, (AccountId, Balance)] = mutable.HashMap.empty[UserId, (AccountId, Balance)]

    override def addUser(name: String): Boolean = if (users.contains(name)) false else {
      users += (name -> newId)
      true
    }

    override def getUserIdByName(name: String): Either[UserError, UserId] =
      if (users.contains(name)) Right(users(name)) else Left(NameNotFound(name))

    override def getAllUsersIds: Either[UserError, List[UserId]] =
      if (users.isEmpty) Left(new UserListIsEmpty) else Right(users.values.toList)

    override def getAllUsersNames: Either[UserError, List[String]] =
      if (users.isEmpty) Left(new UserListIsEmpty) else Right(users.keySet.toList)

    override def createAccount(userId: UserId): Either[AccountingError, AccountId] =
      if (accounts.contains(userId)) {
        Left(UserAlreadyHasAccount(userId))
      } else if (!getAllUsersIds.getOrElse(List.empty[UserId]).contains(userId)) {
        Left(UserNotFound(userId))
      } else {
        val id = newId
        accounts += (userId -> (id, 0.0))
        Right(id)
      }

    override def getAccountIdByUser(userId: UserId): Either[AccountingError, AccountId] =
      if (accounts.contains(userId)) Right(accounts(userId)._1) else Left(UserNotFound(userId))

    override def balance(userId: UserId): Either[AccountingError, Balance] =
      if (accounts.contains(userId)) Right(accounts(userId)._2) else Left(UserNotFound(userId))

    override def put(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance] =
      if (amount < 0) {
        Left(AmountLessThanZero(amount))
      } else if (!accounts.contains(userId)) {
        Left(UserNotFound(userId))
      } else {
        changeBalance(userId, amount)
      }

    override def charge(userId: UserId, amount: BigDecimal): Either[AccountingError, Balance] =
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