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
    def getUserIdByName(name: String): F[UserError, UserId]
    def getAllUsersIds: F[UserError, List[UserId]]
    def getAllUsersNames: F[UserError, List[String]]
  }

  type Balance   = BigDecimal
  type AccountId = UUID
  trait AccountService[F[+_, +_]] {
    def createAccount(userId: UserId): F[AccountingError, AccountId]
    def getAccountIdByUser(userId: UserId): F[AccountingError, AccountId]
    def balance(userId: UserId): F[AccountingError, Balance]
    def put(userId: UserId, amount: BigDecimal): F[AccountingError, Balance]
    def charge(userId: UserId, amount: BigDecimal): F[AccountingError, Balance]
  }

  trait BankService[F[+_, +_]] extends UserService[F] with AccountService[F] {}

  object BankService {
    def addUser[F[+_, +_]](name: String)(implicit interpreter: BankService[F]): Boolean =
      interpreter.addUser(name)
    def getUserIdByName[F[+_, +_]](name: String)(implicit interpreter: BankService[F]): F[UserError, UserId] =
      interpreter.getUserIdByName(name)
    def getAllUsersIds[F[+_, +_]](implicit interpreter: BankService[F]): F[UserError, List[UserId]] =
      interpreter.getAllUsersIds
    def getAllUsersNames[F[+_, +_]](implicit interpreter: BankService[F]): F[UserError, List[String]] =
      interpreter.getAllUsersNames

    def createAccount[F[+_, +_]](userId: UserId)(implicit interpreter: BankService[F]): F[AccountingError, AccountId] =
      interpreter.createAccount(userId)
    def getAccountIdByUser[F[+_, +_]](
        userId: UserId
    )(implicit interpreter: BankService[F]): F[AccountingError, AccountId] =
      interpreter.getAccountIdByUser(userId)
    def balance[F[+_, +_]](userId: UserId)(implicit interpreter: BankService[F]): F[AccountingError, Balance] =
      interpreter.balance(userId)
    def put[F[+_, +_]](userId: UserId, amount: BigDecimal)(
        implicit interpreter: BankService[F]
    ): F[AccountingError, Balance] =
      interpreter.put(userId, amount)
    def charge[F[+_, +_]](userId: UserId, amount: BigDecimal)(
        implicit interpreter: BankService[F]
    ): F[AccountingError, Balance] =
      interpreter.charge(userId, amount)
  }
}
