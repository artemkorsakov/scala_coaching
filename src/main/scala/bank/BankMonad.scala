package bank

import bank.BankService.{AccountingError, Balance, IdGenerator, _}
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

object BankMonad extends IdGenerator {
  trait BiMonad[F[_]]  {
    def addUser(name: String): F[Boolean]
    def getAllUsersIds: F[Either[UserError, List[UserId]]]
    def getAllUsersNames: F[Either[UserError, List[String]]]
    def getUserIdByName(name: String): F[Either[UserError, UserId]]

    def createAccount(userId: UserId): F[Either[AccountingError, AccountId]]
    def getAccountIdByUser(userId: UserId): F[Either[AccountingError, AccountId]]
    def balance(userId: UserId): F[Either[AccountingError, Balance]]
    def put(userId: UserId, amount: BigDecimal): F[Either[AccountingError, Balance]]
    def charge(userId: UserId, amount: BigDecimal): F[Either[AccountingError, Balance]]
  }

  def addUser[F[_]: Monad](name: String)(implicit interpreter: BiMonad[F]): F[Boolean] = interpreter.addUser(name)
  def getAllUsersIds[F[_]: Monad](implicit interpreter: BiMonad[F]): F[Either[UserError, List[UserId]]] = interpreter.getAllUsersIds
  def getAllUsersNames[F[_]: Monad](implicit interpreter: BiMonad[F]): F[Either[UserError, List[String]]] = interpreter.getAllUsersNames
  def getUserIdByName[F[_]: Monad](name: String)(implicit interpreter: BiMonad[F]): F[Either[UserError, UserId]] = interpreter.getUserIdByName(name)

  def createAccount[F[_]: Monad](userId: UserId)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, AccountId]] = interpreter.createAccount(userId)
  def getAccountIdByUser[F[_]: Monad](userId: UserId)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, AccountId]] = interpreter.getAccountIdByUser(userId)
  def balance[F[_]: Monad](userId: UserId)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, Balance]] = interpreter.balance(userId)
  def put[F[_]: Monad](userId: UserId, amount: BigDecimal)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, Balance]] = interpreter.put(userId, amount)
  def charge[F[_]: Monad](userId: UserId, amount: BigDecimal)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, Balance]] = interpreter.charge(userId, amount)

  def balance[F[_]: Monad](name: String)(implicit interpreter: BiMonad[F]): F[Either[AccountingError, Balance]] = {
    for {
      id <- interpreter.getUserIdByName(name)
      newBalance <- interpreter.balance(id.getOrElse(newId))
    } yield {
      newBalance
    }
  }

  def put[F[_]: Monad](name: String, amount: BigDecimal)(implicit interpreter: BiMonad[F]): F[Either[Error, Balance]] = {
    for {
      id <- interpreter.getUserIdByName(name)
      newBalance <- interpreter.put(id.getOrElse(newId), amount)
    } yield {
      newBalance
    }
  }

  def charge[F[_]: Monad](name: String, amount: BigDecimal)(implicit interpreter: BiMonad[F]): F[Either[Error, Balance]] = {
    for {
      id <- interpreter.getUserIdByName(name)
      newBalance <- interpreter.charge(id.getOrElse(newId), amount)
    } yield {
      newBalance
    }
  }

}
