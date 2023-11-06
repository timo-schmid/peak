package ch.timo_schmid.cmf.db

import cats.implicits._
import cats.effect.{Resource, Async}
import _root_.doobie._
import _root_.doobie.implicits._
import _root_.doobie.hikari.HikariTransactor
import _root_.doobie.hikari.HikariTransactor.newHikariTransactor
import _root_.doobie.util.ExecutionContexts.fixedThreadPool
import org.flywaydb.core.Flyway
import javax.sql.DataSource
import scala.concurrent.ExecutionContext

package object doobie:

  private val JdbcDriver   = "org.postgresql.Driver"
  private val JdbcUrl      = "jdbc:postgresql://localhost:5432/postgres"
  private val JdbcUser     = "timo"
  private val JdbcPassword = null

  given databaseConnection[F[_]: Async]: DatabaseConnection[F, ConnectionIO] =
    new DatabaseConnection[F, ConnectionIO]:

      override def resource: Resource[F, Database[F, ConnectionIO]] =
        for {
          executionContext <- fixedThreadPool[F](32)
          transactor       <- hikariTransactor(executionContext)
          _                <- Resource.eval(transactor.configure(runFlyway))
        } yield asDatabase(transactor)

      private def hikariTransactor(
          ec: ExecutionContext
      ): Resource[F, HikariTransactor[F]] =
        newHikariTransactor[F](
          JdbcDriver,
          JdbcUrl,
          JdbcUser,
          JdbcPassword,
          ec
        )

      private def runFlyway(dataSource: DataSource): F[Unit] =
        Async[F].delay(
          Flyway
            .configure()
            .dataSource(dataSource)
            .locations("classpath:migrations-doobie")
            .load()
            .migrate()
        )

      private def asDatabase(
          xa: HikariTransactor[F]
      ): Database[F, ConnectionIO] =
        new Database[F, ConnectionIO]:

          override def transact[A](dbio: ConnectionIO[A]): F[A] =
            dbio.transact(xa)

          override def stream[A](
              stream: fs2.Stream[ConnectionIO, A]
          ): fs2.Stream[F, A] =
            stream.transact(xa)
