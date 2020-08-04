package bank

import bank.BankService.{ AccountId, Balance, UserId }

trait MyBIOTypeclass[F[+_, +_]] {
  def addUser(name: String): Boolean
  def getUserIdByName(name: String): F[Error, UserId]
  def getAllUsersIds: F[Error, List[UserId]]
  def getAllUsersNames: F[Error, List[String]]

  def createAccount(userId: UserId): F[Error, AccountId]
  def getAccountIdByUser(userId: UserId): F[Error, AccountId]
  def balance(userId: UserId): F[Error, Balance]
  def put(userId: UserId, amount: BigDecimal): F[Error, Balance]
  def charge(userId: UserId, amount: BigDecimal): F[Error, Balance]

  def balance(name: String): F[Error, Balance]
  def put(name: String, amount: BigDecimal): F[Error, Balance]
  def charge(name: String, amount: BigDecimal): F[Error, Balance]
  def chargeAll(users: List[(String, Balance)]): F[List[Error], List[Balance]]
}

object MyBIOTypeclass {
  def addUser[F[+_, +_]](name: String)(implicit interpreter: MyBIOTypeclass[F]): Boolean =
    interpreter.addUser(name)
  def getUserIdByName[F[+_, +_]](name: String)(implicit interpreter: MyBIOTypeclass[F]): F[Error, UserId] =
    interpreter.getUserIdByName(name)
  def getAllUsersIds[F[+_, +_]](implicit interpreter: MyBIOTypeclass[F]): F[Error, List[UserId]] =
    interpreter.getAllUsersIds
  def getAllUsersNames[F[+_, +_]](implicit interpreter: MyBIOTypeclass[F]): F[Error, List[String]] =
    interpreter.getAllUsersNames

  def createAccount[F[+_, +_]](userId: UserId)(implicit interpreter: MyBIOTypeclass[F]): F[Error, AccountId] =
    interpreter.createAccount(userId)
  def getAccountIdByUser[F[+_, +_]](userId: UserId)(implicit interpreter: MyBIOTypeclass[F]): F[Error, AccountId] =
    interpreter.getAccountIdByUser(userId)
  def balance[F[+_, +_]](userId: UserId)(implicit interpreter: MyBIOTypeclass[F]): F[Error, Balance] =
    interpreter.balance(userId)
  def put[F[+_, +_]](userId: UserId, amount: BigDecimal)(implicit interpreter: MyBIOTypeclass[F]): F[Error, Balance] =
    interpreter.put(userId, amount)
  def charge[F[+_, +_]](userId: UserId, amount: BigDecimal)(
      implicit interpreter: MyBIOTypeclass[F]
  ): F[Error, Balance] =
    interpreter.charge(userId, amount)

  def balance[F[+_, +_]](name: String)(implicit interpreter: MyBIOTypeclass[F]): F[Error, Balance] =
    interpreter.balance(name)
  def put[F[+_, +_]](name: String, amount: BigDecimal)(implicit interpreter: MyBIOTypeclass[F]): F[Error, Balance] =
    interpreter.put(name, amount)
  def charge[F[+_, +_]](name: String, amount: BigDecimal)(implicit interpreter: MyBIOTypeclass[F]): F[Error, Balance] =
    interpreter.charge(name, amount)
  def chargeAll[F[+_, +_]](users: List[(String, Balance)])(
      implicit interpreter: MyBIOTypeclass[F]
  ): F[List[Error], List[Balance]] = interpreter.chargeAll(users)
}
