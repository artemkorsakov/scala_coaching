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

    assert(p.value.unsafeRunSync() === Right(3))
  }

  "Bank methods " should " returns correct results" in {
    val app: BankApplication = new BankApplication
    val allUsersIds1         = app.getAllUsersIds.value.unsafeRunSync()
    assert(allUsersIds1.isLeft)
    assert(!allUsersIds1.isRight)
    val error1 = allUsersIds1.left.getOrElse(UserListIsEmpty)
    assert(error1.isInstanceOf[UserListIsEmpty])

    val allUsersNames1 = app.getAllUsersNames.value.unsafeRunSync()
    assert(allUsersNames1.isLeft)
    assert(!allUsersNames1.isRight)
    val error2 = allUsersNames1.left.getOrElse(UserListIsEmpty)
    assert(error2.isInstanceOf[UserListIsEmpty])

    val eitherId1 = app.getUserIdByName("John").value.unsafeRunSync()
    assert(eitherId1.isLeft)
    assert(!eitherId1.isRight)
    assert(eitherId1.left.getOrElse() === NameNotFound("John"))

    app.addUser("John")

    val eitherId2 = app.getUserIdByName("John").value.unsafeRunSync()
    assert(!eitherId2.isLeft)
    assert(eitherId2.isRight)
    val idJohn: UUID = eitherId2.getOrElse(newId)
    assert(idJohn.toString.nonEmpty)

    val allUsersIds2 = app.getAllUsersIds.value.unsafeRunSync()
    assert(!allUsersIds2.isLeft)
    assert(allUsersIds2.isRight)
    assert(allUsersIds2.getOrElse() === List(idJohn))

    val allUsersNames2 = app.getAllUsersNames.value.unsafeRunSync()
    assert(!allUsersNames2.isLeft)
    assert(allUsersNames2.isRight)
    assert(allUsersNames2.getOrElse() === List("John"))

    val eitherId3 = app.getUserIdByName("Mike").value.unsafeRunSync()
    assert(eitherId3.isLeft)
    assert(!eitherId3.isRight)
    assert(eitherId3.left.getOrElse() === NameNotFound("Mike"))

    app.addUser("Mike")

    val eitherId4 = app.getUserIdByName("Mike").value.unsafeRunSync()
    assert(!eitherId4.isLeft)
    assert(eitherId4.isRight)
    val idMike: UUID = eitherId4.getOrElse(newId)
    assert(idMike.toString.nonEmpty)
    assert(idMike != idJohn)

    val allUsersIds3 = app.getAllUsersIds.value.unsafeRunSync()
    assert(!allUsersIds3.isLeft)
    assert(allUsersIds3.isRight)
    assert(allUsersIds3.getOrElse(List.empty[UUID]).length === 2)
    assert(allUsersIds3.getOrElse(List.empty[UUID]).contains(idJohn))
    assert(allUsersIds3.getOrElse(List.empty[UUID]).contains(idMike))

    val allUsersNames3 = app.getAllUsersNames.value.unsafeRunSync()
    assert(!allUsersNames3.isLeft)
    assert(allUsersNames3.isRight)
    assert(allUsersNames3.getOrElse(List.empty[String]).length === 2)
    assert(allUsersNames3.getOrElse(List.empty[String]).contains("John"))
    assert(allUsersNames3.getOrElse(List.empty[String]).contains("Mike"))

    val eitherId5 = app.getUserIdByName("Pete").value.unsafeRunSync()
    assert(eitherId5.isLeft)
    assert(!eitherId5.isRight)
    assert(eitherId5.left.getOrElse() === NameNotFound("Pete"))

    val crAcc1 = app.createAccount(idJohn).value.unsafeRunSync()
    assert(!crAcc1.isLeft)
    assert(crAcc1.isRight)
    val accountIdJohn: UUID = crAcc1.getOrElse(newId)
    assert(accountIdJohn.toString.nonEmpty)

    val crAcc2 = app.createAccount(idJohn).value.unsafeRunSync()
    assert(crAcc2.isLeft)
    assert(!crAcc2.isRight)
    assert(crAcc2.left.getOrElse() === UserAlreadyHasAccount(idJohn))

    val crAcc3 = app.createAccount(idMike).value.unsafeRunSync()
    assert(!crAcc3.isLeft)
    assert(crAcc3.isRight)
    val accountIdMike: UUID = crAcc3.getOrElse(newId)
    assert(accountIdMike.toString.nonEmpty)
    assert(accountIdMike != accountIdJohn)

    val random: UUID = newId
    val crAcc4       = app.createAccount(random).value.unsafeRunSync()
    assert(crAcc4.isLeft)
    assert(!crAcc4.isRight)
    assert(crAcc4.left.getOrElse() === UserNotFound(random))

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
    val result1 = app.chargeAll(list1).value.unsafeRunSync()
    assert(result1.isLeft)
    assert(!result1.isRight)
    assert(
      result1.left.getOrElse(List.empty[Error]) === List(
          BalanceTooLow(0.0),
          UserNotFound(idPete),
          NameNotFound("Paul")
        )
    )
    assert(app.balance("John").value.unsafeRunSync() === Right(60.92))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(0.0))

    assert(app.put("Mike", 15.0).value.unsafeRunSync() === Right(15.0))
    val list2: List[(String, Balance)] = List(("John", 13.56), ("Mike", 0.5), ("John", 0.53), ("John", -7.51))
    val result2                        = app.chargeAll(list2).value.unsafeRunSync()
    assert(!result2.isLeft)
    assert(result2.isRight)
    assert(result2.getOrElse(List.empty[Balance]) === List(47.36, 14.50, 46.83, 54.34))
    assert(app.balance("John").value.unsafeRunSync() === Right(54.34))
    assert(app.balance("Mike").value.unsafeRunSync() === Right(14.50))
  }

}
