package ch.timo_schmid.cmf.db.doobie

import cats.Id
import cats.Show
import cats.effect.Clock
import cats.effect.Sync
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.*
import ch.timo_schmid.cmf.db.Database
import ch.timo_schmid.cmf.db.DatabaseQueries
import doobie.ConnectionIO
import doobie.Read
import doobie.Write
import doobie.util.fragment.Fragment
import fs2.Stream
import org.typelevel.log4cats.Logger
import scala.concurrent.duration.FiniteDuration

class DoobieStorage[F[_]: Sync: Clock, Data[_[_]], KeyType](db: Database[F, ConnectionIO])(using
    databaseQueries: DatabaseQueries[Data[Id], KeyType],
    readUser: Read[Data[Id]],
    log: Logger[F]
) extends Storage[F, Data, KeyType] {

  override def list: Stream[F, Data[Id]] =
    for
      fragment  <- Stream.emit(databaseQueries.select)
      startTime <- Stream.eval(Clock[F].monotonic)
      result    <- db.stream(fragment.query[Data[Id]].stream)
      endTime   <- Stream.eval(Clock[F].monotonic)
      _         <- Stream.eval(logQueryCompleted(fragment, endTime.minus(startTime)))
    yield result

  override def byKey(key: KeyType): F[Option[Data[Id]]] =
    run[Option[Data[Id]]](databaseQueries.byKey(key)) { fragment =>
      db.transact(
        fragment
          .query[Data[Id]]
          .option
      )
    }

  override def create(data: Data[Id]): F[Data[Id]] =
    run[Data[Id]](databaseQueries.create(data)) { fragment =>
      db.transact(
        fragment.update
          .withUniqueGeneratedKeys[Data[Id]](databaseQueries.fieldNames: _*)
      )
    }

  override def update(key: KeyType, updated: Data[Id]): F[Option[Data[Id]]] =
    run[Int](databaseQueries.update(key, updated)) { fragment =>
      db.transact(
        fragment.update.run
      )
    }.flatMap(checkAffectedRows(key)) *> byKey(key)

  override def delete(key: KeyType): F[Unit] =
    run[Int](databaseQueries.delete(key)) { fragment =>
      db.transact(
        databaseQueries
          .delete(key)
          .update
          .run
      )
    }.flatMap(checkAffectedRows(key))

  private def run[A](fragment: Fragment)(runQuery: Fragment => F[A]): F[A] =
    Clock[F]
      .timed(runQuery(fragment))
      .attempt
      .flatMap(handleError[A](fragment))

  private def handleError[A](fragment: Fragment)(
      result: Either[Throwable, (FiniteDuration, A)]
  ): F[A] =
    result match
      case Right((duration, result)) =>
        logQueryCompleted(fragment, duration).as(result)
      case Left(error)               =>
        logQueryError(fragment, error) *> error.raiseError[F, A]

  private def logQueryCompleted[A](fragment: Fragment, duration: FiniteDuration): F[Unit] =
    log.info(s"Query '${fragment.show}' took $duration")

  private def logQueryError(fragment: Fragment, error: Throwable): F[Unit] =
    log.error(error)(s"Query '${fragment.show}' resulted in an error: ${error.getMessage}")

  private def checkAffectedRows(key: KeyType)(affectedRows: Int): F[Unit] =
    affectedRows match
      case _: 0 => raiseError(s"Not found: $key")
      case _: 1 => Sync[F].unit
      case _    => raiseError(s"More than 1 row was affected for key $key")

  private def raiseError(message: String): F[Unit] =
    for {
      _     <- Sync[F].delay(println(s"Error: $message"))
      error <- Sync[F].pure(new RuntimeException(message))
      _     <- Sync[F].delay(error.printStackTrace())
      xs    <- list.compile.toList
      _     <- Sync[F].delay(println(s"List: ${xs.size}"))
      _     <- xs.traverse_(data => Sync[F].delay[Unit](println(s"List: $data")))
      _     <- Sync[F].raiseError(error)
    } yield ()

  private given Show[Fragment] =
    _.internals.sql

}

object DoobieStorage:

  def provider[F[_]: Sync, Data[_[_]], KeyType](using
      databaseQueries: DatabaseQueries[Data[Id], KeyType],
      writeKey: Write[KeyType],
      readData: Read[Data[Id]],
      writeData: Write[Data[Id]],
      loggerProvider: LoggerProvider[F]
  ): Provider[F, Database[F, ConnectionIO], Storage[F, Data, KeyType]] =
    db =>
      loggerProvider
        .create(getClass)
        .map { log =>
          given Logger[F] = log
          new DoobieStorage[F, Data, KeyType](db)
        }
