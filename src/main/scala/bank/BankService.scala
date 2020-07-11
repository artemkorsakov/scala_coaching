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

}
