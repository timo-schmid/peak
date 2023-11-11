package ch.timo_schmid.cmf.db.doobie

import cats.Id
import cats.effect.Sync
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.db.Database
import ch.timo_schmid.cmf.db.DatabaseQueries
import doobie.ConnectionIO
import doobie.Read
import fs2.Stream

class DoobieStorage[F[_]: Sync, Data[_[_]], KeyType](using
    db: Database[F, ConnectionIO],
    databaseQueries: DatabaseQueries[Data[Id], KeyType],
    readUser: Read[Data[Id]]
) extends Storage[F, Data, KeyType] {

  override def list: Stream[F, Data[Id]] =
    db.stream(
      databaseQueries.select
        .query[Data[Id]]
        .stream
    )

  override def byKey(key: KeyType): F[Option[Data[Id]]] =
    db.transact(
      databaseQueries
        .byKey(key)
        .query[Data[Id]]
        .option
    )

  override def create(data: Data[Id]): F[Data[Id]] =
    db.transact(
      databaseQueries
        .create(data)
        .update
        .withUniqueGeneratedKeys[Data[Id]](databaseQueries.fieldNames: _*)
    )

  override def update(key: KeyType, updated: Data[Id]): F[Option[Data[Id]]] =
    db.transact(
      databaseQueries
        .update(key, updated)
        .update
        .run
    ).flatMap(checkAffectedRows(key)) *> byKey(key)

  override def delete(key: KeyType): F[Unit] =
    db.transact(
      databaseQueries
        .delete(key)
        .update
        .run
    ).flatMap(checkAffectedRows(key))

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

}
