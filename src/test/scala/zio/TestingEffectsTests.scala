package zio

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers
import zio.MyDB._
import zio.console._

class TestingEffectsTests extends AnyFunSuiteLike with Matchers {
  test("test Testing Effects.") {
    val en = for {
      env <- ZIO.environment[Int]
      _   <- putStrLn(s"The value of the environment is: $env")
    } yield env

    println(en)

    final case class Config(server: String, port: Int)

    val configString: URIO[Config, String] =
      for {
        server <- ZIO.access[Config](_.server)
        port   <- ZIO.access[Config](_.port)
      } yield s"Server: $server, port: $port"
    println(configString)

    trait DatabaseOps {
      def getTableNames: Task[List[String]]
      def getColumnNames(table: String): Task[List[String]]
    }

    val tablesAndColumns: ZIO[DatabaseOps, Throwable, (List[String], List[String])] = {
      for {
        tables  <- ZIO.accessM[DatabaseOps](_.getTableNames)
        columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("user_table"))
      } yield (tables, columns)
    }
    println(tablesAndColumns)

    val square: URIO[Int, Int] =
      for {
        env <- ZIO.environment[Int]
      } yield env * env

    val result: UIO[Int] = square.provide(42)
    println(result)
  }

  test("test Testing Effects. Environmental Effects") {
    def code: RIO[Database, Unit] = ???

    def code2: Task[Unit] =
      code.provide(TestDatabase)
  }
}
