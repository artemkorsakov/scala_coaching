package bank

object BankService {
  import java.util.UUID

  sealed trait UserError                extends Error
  case class NameNotFound(name: String) extends UserError
  case class UserListIsEmpty()          extends UserError

  sealed trait AccountingError                       extends Error
  case class UserNotFound(userId: UserId)            extends AccountingError
  case class UserAlreadyHasAccount(userId: UserId)   extends AccountingError
  case class AccountIdNotFound(accountId: AccountId) extends AccountingError
  case class BalanceTooLow(currentBalance: Balance)  extends AccountingError
  case class AmountLessThanZero(amount: BigDecimal)  extends AccountingError

  trait IdGenerator {
    def newId: UUID = UUID.randomUUID()
  }

  type UserId = UUID
  trait UserService[F[+_, +_]] {
    def addUser(name: String): Boolean
    def getUserIdByName(name: String): F[Error, UserId]
    def getAllUsersIds: F[Error, List[UserId]]
    def getAllUsersNames: F[Error, List[String]]
  }

  type Balance   = BigDecimal
  type AccountId = UUID
  trait AccountService[F[+_, +_]] {
    def createAccount(userId: UserId): F[Error, AccountId]
    def getAccountIdByUser(userId: UserId): F[Error, AccountId]
    def balance(userId: UserId): F[Error, Balance]
    def put(userId: UserId, amount: BigDecimal): F[Error, Balance]
    def charge(userId: UserId, amount: BigDecimal): F[Error, Balance]
  }

  trait BankService[F[+_, +_]] extends UserService[F] with AccountService[F] {}

  object BankService {
    def addUser[F[+_, +_]](name: String)(implicit interpreter: BankService[F]): Boolean =
      interpreter.addUser(name)
    def getUserIdByName[F[+_, +_]](name: String)(implicit interpreter: BankService[F]): F[Error, UserId] =
      interpreter.getUserIdByName(name)
    def getAllUsersIds[F[+_, +_]](implicit interpreter: BankService[F]): F[Error, List[UserId]] =
      interpreter.getAllUsersIds
    def getAllUsersNames[F[+_, +_]](implicit interpreter: BankService[F]): F[Error, List[String]] =
      interpreter.getAllUsersNames

    def createAccount[F[+_, +_]](userId: UserId)(implicit interpreter: BankService[F]): F[Error, AccountId] =
      interpreter.createAccount(userId)
    def getAccountIdByUser[F[+_, +_]](userId: UserId)(implicit interpreter: BankService[F]): F[Error, AccountId] =
      interpreter.getAccountIdByUser(userId)
    def balance[F[+_, +_]](userId: UserId)(implicit interpreter: BankService[F]): F[Error, Balance] =
      interpreter.balance(userId)
    def put[F[+_, +_]](userId: UserId, amount: BigDecimal)(implicit interpreter: BankService[F]): F[Error, Balance] =
      interpreter.put(userId, amount)
    def charge[F[+_, +_]](userId: UserId, amount: BigDecimal)(implicit interpreter: BankService[F]): F[Error, Balance] =
      interpreter.charge(userId, amount)
  }

}
