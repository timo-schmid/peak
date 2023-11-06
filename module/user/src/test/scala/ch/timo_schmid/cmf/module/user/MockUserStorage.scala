package ch.timo_schmid.cmf.module.user

import cats.Id
import cats.implicits.*
import cats.effect.Sync
import cats.effect.kernel.Concurrent
import ch.timo_schmid.cmf.api.Storage
import ch.timo_schmid.cmf.rest.*
import ch.timo_schmid.cmf.module.user.User
import ch.timo_schmid.cmf.db.{Database, DatabaseConnection}
import fs2.*

import java.util.UUID

class MockUserStorage[F[_]: Sync](using Merge[User]) extends Storage[F, UserId, User] {

  private val mockUsers: Seq[User[Id]] = Seq(
    User[Id](
      UserId(UUID.randomUUID()),
      "alice",
      Email("alice", "gmail.com")
    ),
    User[Id](
      UserId(UUID.randomUUID()),
      "bob",
      Email("bob", "bob.com")
    )
  )

  override def list: Stream[F, User.Full] =
    Stream.emits[F, User.Full](mockUsers)

  override def byKey(key: UserId): F[Option[User.Full]] =
    list
      .filter(_.userId == key)
      .head
      .compile
      .toList
      .map(_.headOption)

  override def create(thing: User.Full): F[User.Full] =
    Sync[F].delay(thing)

  override def update(
      key: UserId,
      updated: User.Full
  ): F[Option[User.Full]] =
    byKey(key).flatMap {
      case Some(_) => Sync[F].delay(Some(updated))
      case None    => Sync[F].delay(None)
    }

  override def delete(key: UserId): F[Unit] =
    Sync[F].unit

}
