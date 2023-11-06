package ch.timo_schmid.cmf.module.user

import cats.Id
import cats.effect.Sync
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.core.entity.Merge
import ch.timo_schmid.cmf.module.user.User.UserId
import fs2.*
import java.util.UUID

class MockUserStorage[F[_]: Sync](using Merge[User]) extends Storage[F, User, UserId] {

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
      .filter(_.id == key)
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
