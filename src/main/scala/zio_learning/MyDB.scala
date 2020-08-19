package zio_learning

import java.util.UUID

import zio._

object MyDB {
  type UserID = UUID
  case class UserProfile(firstName: String, lastName: String)

  object Database {
    trait Service {
      def lookup(id: UserID): Task[UserProfile]
      def update(id: UserID, profile: UserProfile): Task[Unit]
    }
  }
  trait Database {
    def database: Database.Service
  }

  object db {
    def lookup(id: UserID): RIO[Database, UserProfile] =
      ZIO.accessM(_.database.lookup(id))

    def update(id: UserID, profile: UserProfile): RIO[Database, Unit] =
      ZIO.accessM(_.database.update(id, profile))
  }

  trait DatabaseLive extends Database {
    def database: Database.Service =
      new Database.Service {
        def lookup(id: UserID): Task[UserProfile]                = ???
        def update(id: UserID, profile: UserProfile): Task[Unit] = ???
      }
  }
  object DatabaseLive extends DatabaseLive

  class TestService extends Database.Service {
    private var map: Map[UserID, UserProfile] = Map()

    def setTestData(map0: Map[UserID, UserProfile]): Task[Unit] =
      Task { map = map0 }

    def getTestData: Task[Map[UserID, UserProfile]] =
      Task(map)

    def lookup(id: UserID): Task[UserProfile] =
      Task(map(id))

    def update(id: UserID, profile: UserProfile): Task[Unit] =
      Task.effect { map = map + (id -> profile) }
  }
  trait TestDatabase extends Database {
    val database: TestService = new TestService
  }
  object TestDatabase extends TestDatabase
}
