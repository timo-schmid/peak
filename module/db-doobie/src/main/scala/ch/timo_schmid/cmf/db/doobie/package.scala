package ch.timo_schmid.cmf.db

import _root_.doobie.*
import _root_.doobie.hikari.HikariTransactor
import _root_.doobie.hikari.HikariTransactor.newHikariTransactor
import _root_.doobie.implicits.*
import _root_.doobie.util.ExecutionContexts.fixedThreadPool
import cats.effect.Async
import cats.effect.Resource
import cats.implicits.*
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import scala.concurrent.ExecutionContext

package object doobie:

  /*
  private val JdbcDriver   = "org.postgresql.Driver"
  private val JdbcUrl      = "jdbc:postgresql://localhost:5432/postgres"
  private val JdbcUser     = "timo"
  private val JdbcPassword = null
   */

  given databaseConnection[F[_]: Async](using
      config: DatabaseConfig
  ): DatabaseConnection[F, ConnectionIO] =
    new DatabaseConnection[F, ConnectionIO]:

      override def resource: Resource[F, Database[F, ConnectionIO]] =
        for {
          executionContext <- fixedThreadPool[F](config.numThreads)
          transactor       <- hikariTransactor(executionContext)
          _                <- Resource.eval(transactor.configure(runFlyway))
        } yield asDatabase(transactor)

      private def hikariTransactor(
          ec: ExecutionContext
      )(using config: DatabaseConfig): Resource[F, HikariTransactor[F]] =
        newHikariTransactor[F](
          config.driver,
          config.jdbcUrl,
          config.username,
          config.password,
          ec
        )

      private def runFlyway(dataSource: DataSource): F[Unit] =
        Async[F]
          .delay(
            Flyway
              .configure()
              .dataSource(dataSource)
              .locations("classpath:migrations-doobie")
              .load()
              .migrate()
          )
          .void

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
