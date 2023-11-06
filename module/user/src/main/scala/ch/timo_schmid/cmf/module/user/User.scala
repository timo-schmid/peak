package ch.timo_schmid.cmf.module.user

import cats.Id
import cats.Show
import cats.data.Tuple2K
import cats.effect.{Concurrent, Sync, Async}
import ch.timo_schmid.cmf.api.*
import ch.timo_schmid.cmf.db.*
import ch.timo_schmid.cmf.db.doobie.*
import ch.timo_schmid.cmf.rest.*
import ch.timo_schmid.cmf.rest.http4s.*
import _root_.doobie.*
import _root_.doobie.implicits.*
import org.http4s.EntityDecoder
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import shapeless3.deriving.~>

import java.util.UUID

case class User[F[_]](
    userId: F[UserId],
    login: F[String],
    email: F[Email]
) derives Merge,
      DoobieDatabaseFields

object User:

  type Full    = User[Id]
  type Partial = User[Option]

  given toPartial(using fk: FunctorK[User]): ToPartial[User] =
    full => fk.mapK[Id, Option](full)([A] => (a: Id[A]) => Some[A](a))

  given showUserId: Show[UserId] =
    userId => userId.toString

  given userKey: Key[User, UserId] =
    new Key[User, UserId]:

      override def key(entity: User[Id]): UserId =
        entity.userId

      override def unapply(string: String): Option[UserId] =
        UserId.unapply(string)

  given storage[F[_]: Sync](using
      Database[F, ConnectionIO]
  ): Storage[F, UserId, User] =
    new DoobieStorage[F, UserId, User]

  given restHttp4sRoutes[F[_]: Async: Sync](using
      Database[F, ConnectionIO]
  ): RESTHttp4sRoutes[F, UserId, User] =
    given RESTHttp4sHandler[F, UserId, User] =
      new RESTHttp4sHandler[F, UserId, User]
    new RESTHttp4sRoutes[F, UserId, User]

  given dbQueries: DatabaseQueries[UserId, User[Id]] =
    new DoobieDatabaseQueries[UserId, User]
