package bank

object BankService {
  import java.util.UUID

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
  trait UserService[F[+_, +_]] {
    def addUser(name: String): Boolean
    def getUserIdByName(name: String): F[UserError, UserId]
    def getAllUsersIds: F[UserError, List[UserId]]
    def getAllUsersNames: F[UserError, List[String]]
  }

  type Balance = BigDecimal
  type AccountId = UUID
  trait AccountService[F[+_, +_]] {
    def createAccount(userId: UserId): F[AccountingError, AccountId]
    def getAccountIdByUser(userId: UserId): F[AccountingError, AccountId]
    def balance(userId: UserId): F[AccountingError, Balance]
    def put(userId: UserId, amount: BigDecimal): F[AccountingError, Balance]
    def charge(userId: UserId, amount: BigDecimal): F[AccountingError, Balance]
  }

  trait BankService[F[+_, +_]] extends UserService[F] with AccountService[F] {}
}
