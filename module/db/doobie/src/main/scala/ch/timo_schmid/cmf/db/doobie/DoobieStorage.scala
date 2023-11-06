package ch.timo_schmid.cmf.db.doobie

import cats.Id
import cats.effect.Sync
import cats.implicits.*
import ch.timo_schmid.cmf.api.Storage
import ch.timo_schmid.cmf.db.Database
import ch.timo_schmid.cmf.db.DatabaseQueries
import doobie.{ConnectionIO, Read, Write}
import fs2.Stream

class DoobieStorage[F[_]: Sync, Key, Data[_[_]]](using
    db: Database[F, ConnectionIO],
    databaseQueries: DatabaseQueries[Key, Data[Id]],
    readUser: Read[Data[Id]],
    writeUser: Write[Data[Id]],
    writeUserId: Write[Key]
) extends Storage[F, Key, Data] {

  override def list: Stream[F, Data[Id]] =
    db.stream(
      databaseQueries.select
        .query[Data[Id]]
        .stream
    )

  override def byKey(key: Key): F[Option[Data[Id]]] =
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
        .query[Data[Id]]
        .unique
    )

  override def update(key: Key, updated: Data[Id]): F[Option[Data[Id]]] =
    db.transact(
      databaseQueries
        .update(key, updated)
        .update
        .run
    ).flatMap(checkAffectedRows(key)) *> byKey(key)

  override def delete(key: Key): F[Unit] =
    db.transact(
      databaseQueries
        .delete(key)
        .update
        .run
    ).flatMap(checkAffectedRows(key))

  private def checkAffectedRows(key: Key)(affectedRows: Int): F[Unit] =
    affectedRows match
      case _: 0 => raiseError(s"Not found: $key")
      case _: 1 => Sync[F].unit
      case _    => raiseError(s"More than 1 row was affected for key $key")

  private def raiseError(message: String): F[Unit] =
    Sync[F].raiseError(new RuntimeException(message))

}
