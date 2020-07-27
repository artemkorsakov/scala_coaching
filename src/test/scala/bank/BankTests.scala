package bank

import java.util.UUID

import bank.BankApp._
import bank.BankService._
import bank.MyBIOImpl.MyBIO
import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpec

class BankTests extends AnyFlatSpec with IdGenerator {

  "MyBIO " should " returns correct results" in {
    val p = for {
      a <- MyBIO(1)
      b <- MyBIO(2)
    } yield {
      a + b
    }

    println(p.value.unsafeRunSync())
  }

  "Bank methods " should " returns correct results" in {
    val app: BankApplication = new BankApplication
    var allUsersIds          = app.getAllUsersIds.value.unsafeRunSync()
    assert(allUsersIds.isLeft)
    assert(!allUsersIds.isRight)
    var error = allUsersIds.left.getOrElse(UserListIsEmpty)
    assert(error.isInstanceOf[UserListIsEmpty])

    var allUsersNames = app.getAllUsersNames.value.unsafeRunSync()
    assert(allUsersNames.isLeft)
    assert(!allUsersNames.isRight)
    error = allUsersIds.left.getOrElse(UserListIsEmpty)
    assert(error.isInstanceOf[UserListIsEmpty])

    var eitherId = app.getUserIdByName("John").value.unsafeRunSync()
    assert(eitherId.isLeft)
    assert(!eitherId.isRight)
    assert(eitherId.left.getOrElse() === NameNotFound("John"))

    app.addUser("John")

    eitherId = app.getUserIdByName("John").value.unsafeRunSync()
    assert(!eitherId.isLeft)
    assert(eitherId.isRight)
    val idJohn: UUID = eitherId.getOrElse(newId)
    assert(idJohn.toString.nonEmpty)

    allUsersIds = app.getAllUsersIds.value.unsafeRunSync()
    assert(!allUsersIds.isLeft)
    assert(allUsersIds.isRight)
    assert(allUsersIds.getOrElse() === List(idJohn))

    allUsersNames = app.getAllUsersNames.value.unsafeRunSync()
    assert(!allUsersNames.isLeft)
    assert(allUsersNames.isRight)
    assert(allUsersNames.getOrElse() === List("John"))

    eitherId = app.getUserIdByName("Mike").value.unsafeRunSync()
    assert(eitherId.isLeft)
    assert(!eitherId.isRight)
    assert(eitherId.left.getOrElse() === NameNotFound("Mike"))

    app.addUser("Mike")

    eitherId = app.getUserIdByName("Mike").value.unsafeRunSync()
    assert(!eitherId.isLeft)
    assert(eitherId.isRight)
    val idMike: UUID = eitherId.getOrElse(newId)
    assert(idMike.toString.nonEmpty)
    assert(idMike != idJohn)

    allUsersIds = app.getAllUsersIds.value.unsafeRunSync()
    assert(!allUsersIds.isLeft)
    assert(allUsersIds.isRight)
    assert(allUsersIds.getOrElse(List.empty[UUID]).length === 2)
    assert(allUsersIds.getOrElse(List.empty[UUID]).contains(idJohn))
    assert(allUsersIds.getOrElse(List.empty[UUID]).contains(idMike))

    allUsersNames = app.getAllUsersNames.value.unsafeRunSync()
    assert(!allUsersNames.isLeft)
    assert(allUsersNames.isRight)
    assert(allUsersNames.getOrElse(List.empty[String]).length === 2)
    assert(allUsersNames.getOrElse(List.empty[String]).contains("John"))
    assert(allUsersNames.getOrElse(List.empty[String]).contains("Mike"))

    eitherId = app.getUserIdByName("Pete").value.unsafeRunSync()
    assert(eitherId.isLeft)
    assert(!eitherId.isRight)
    assert(eitherId.left.getOrElse() === NameNotFound("Pete"))

    var crAcc = app.createAccount(idJohn).value.unsafeRunSync()
    assert(!crAcc.isLeft)
    assert(crAcc.isRight)
    val accountIdJohn: UUID = crAcc.getOrElse(newId)
    assert(accountIdJohn.toString.nonEmpty)

    crAcc = app.createAccount(idJohn).value.unsafeRunSync()
    assert(crAcc.isLeft)
    assert(!crAcc.isRight)
    assert(crAcc.left.getOrElse() === UserAlreadyHasAccount(idJohn))

    crAcc = app.createAccount(idMike).value.unsafeRunSync()
    assert(!crAcc.isLeft)
    assert(crAcc.isRight)
    val accountIdMike: UUID = crAcc.getOrElse(newId)
    assert(accountIdMike.toString.nonEmpty)
    assert(accountIdMike != accountIdJohn)

    val random: UUID = newId
    crAcc = app.createAccount(random).value.unsafeRunSync()
    assert(crAcc.isLeft)
    assert(!crAcc.isRight)
    assert(crAcc.left.getOrElse() === UserNotFound(random))

    assert(app.getAccountIdByUser(idJohn).value.unsafeRunSync() === Right(accountIdJohn))
    assert(app.getAccountIdByUser(idMike).value.unsafeRunSync() === Right(accountIdMike))
    assert(app.getAccountIdByUser(random).value.unsafeRunSync() === Left(UserNotFound(random)))

    assert(app.balance(idJohn).value.unsafeRunSync() === Right(0.0))
    assert(app.balance(idMike).value.unsafeRunSync() === Right(0.0))
    assert(app.balance(random).value.unsafeRunSync() === Left(UserNotFound(random)))

    app.addUser("Pete")

    val idPete: UUID = app.getUserIdByName("Pete").value.unsafeRunSync().getOrElse(UUID.randomUUID())
    assert(app.balance("John").value.unsafeRunSync() === Right(0.0))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(0.0))
    assert(app.balance("Pete").value.unsafeRunSync() === Left(UserNotFound(idPete)))
    assert(app.balance("Paul").value.unsafeRunSync() === Left(NameNotFound("Paul")))

    assert(app.put(idJohn, -0.5).value.unsafeRunSync() === Left(AmountLessThanZero(-0.5)))
    assert(app.put(idJohn, 10.5).value.unsafeRunSync() === Right(10.5))
    assert(app.put(idJohn, 23).value.unsafeRunSync() === Right(33.5))
    assert(app.put(idJohn, -33.5).value.unsafeRunSync() === Left(AmountLessThanZero(-33.5)))
    assert(app.put(idMike, 7.5).value.unsafeRunSync() === Right(7.5))
    assert(app.balance(idJohn).value.unsafeRunSync() === Right(33.5))
    assert(app.balance(idMike).value.unsafeRunSync() === Right(7.5))
    assert(app.put(idPete, 100).value.unsafeRunSync() === Left(UserNotFound(idPete)))

    assert(app.put("John", -0.5).value.unsafeRunSync() === Left(AmountLessThanZero(-0.5)))
    assert(app.put("John", 10.5).value.unsafeRunSync() === Right(44.0))
    assert(app.put("John", 23).value.unsafeRunSync() === Right(67.0))
    assert(app.put("John", -33.5).value.unsafeRunSync() === Left(AmountLessThanZero(-33.5)))
    assert(app.put("Mike", 7.5).value.unsafeRunSync() === Right(15.0))
    assert(app.balance("John").value.unsafeRunSync() === Right(67.0))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(15.0))
    assert(app.put("Pete", 100).value.unsafeRunSync() === Left(UserNotFound(idPete)))
    assert(app.put("Paul", 100).value.unsafeRunSync() === Left(NameNotFound("Paul")))

    assert(app.charge(idJohn, 10.5).value.unsafeRunSync() === Right(56.5))
    assert(app.charge(idJohn, -0.5).value.unsafeRunSync() === Right(57.0))
    assert(app.charge(idJohn, 23).value.unsafeRunSync() === Right(34.0))
    assert(app.charge(idJohn, -33.5).value.unsafeRunSync() === Right(67.5))
    assert(app.charge(idMike, 6.25).value.unsafeRunSync() === Right(8.75))
    assert(app.balance(idJohn).value.unsafeRunSync() === Right(67.5))
    assert(app.charge(idJohn, 100).value.unsafeRunSync() === Left(BalanceTooLow(67.5)))
    assert(app.balance(idMike).value.unsafeRunSync() === Right(8.75))
    assert(app.charge(idMike, 8.76).value.unsafeRunSync() === Left(BalanceTooLow(8.75)))
    assert(app.charge(idMike, 8.75).value.unsafeRunSync() === Right(0.0))
    assert(app.charge(idPete, 100).value.unsafeRunSync() === Left(UserNotFound(idPete)))

    assert(app.charge("John", 0.5).value.unsafeRunSync() === Right(67.0))
    assert(app.put("Mike", 15.0).value.unsafeRunSync() === Right(15.0))
    assert(app.charge("John", 10.5).value.unsafeRunSync() === Right(56.5))
    assert(app.charge("John", -0.5).value.unsafeRunSync() === Right(57.0))
    assert(app.charge("John", 23).value.unsafeRunSync() === Right(34.0))
    assert(app.charge("John", -33.5).value.unsafeRunSync() === Right(67.5))
    assert(app.charge("Mike", 6.25).value.unsafeRunSync() === Right(8.75))
    assert(app.balance("John").value.unsafeRunSync() === Right(67.5))
    assert(app.charge("John", 100).value.unsafeRunSync() === Left(BalanceTooLow(67.5)))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(8.75))
    assert(app.charge("Mike", 8.76).value.unsafeRunSync() === Left(BalanceTooLow(8.75)))
    assert(app.charge("Mike", 8.75).value.unsafeRunSync() === Right(0.0))
    assert(app.charge("Pete", 100).value.unsafeRunSync() === Left(UserNotFound(idPete)))
    assert(app.charge("Paul", 100).value.unsafeRunSync() === Left(NameNotFound("Paul")))

    val list1: List[(String, Balance)] =
      List(("John", 13.56), ("Mike", 0.5), ("John", 0.53), ("John", -7.51), ("Pete", 0.5), ("Paul", 0.5))
    var result = app.chargeAll(list1).value.unsafeRunSync()
    assert(result.isLeft)
    assert(!result.isRight)
    assert(
      result.left.getOrElse(List.empty[Error]) === List(
          BalanceTooLow(0.0),
          UserNotFound(idPete),
          NameNotFound("Paul")
        )
    )
    assert(app.balance("John").value.unsafeRunSync() === Right(60.92))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(0.0))

    assert(app.put("Mike", 15.0).value.unsafeRunSync() === Right(15.0))
    val list2: List[(String, Balance)] = List(("John", 13.56), ("Mike", 0.5), ("John", 0.53), ("John", -7.51))
    result = app.chargeAll(list2).value.unsafeRunSync()
    assert(!result.isLeft)
    assert(result.isRight)
    assert(
      result.getOrElse(List.empty[Balance]) === List(47.36, 14.50, 46.83, 54.34)
    )
    assert(app.balance("John").value.unsafeRunSync() === Right(54.34))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(14.50))
  }

}
