package ch.timo_schmid.cmf.module.user

import cats.Id
import ch.timo_schmid.cmf.codec.circe.CirceCodecs
import ch.timo_schmid.cmf.core.entity.Iso
import ch.timo_schmid.cmf.core.entity.Key
import ch.timo_schmid.cmf.db.DatabaseTable
import ch.timo_schmid.cmf.module.user.User.UserId
import ch.timo_schmid.cmf.rest.http4s.REST
import java.util.UUID
import org.http4s.circe.CirceEntityCodec.*

case class User[F[_]](
    id: F[UserId],
    login: F[String],
    email: F[Email]
) derives REST,
      CirceCodecs

object User extends AllInstances[User, UserId]:

  opaque type UserId = UUID
  object UserId extends Opaque.UUID[UserId]

  opaque type Password = String
  object Password extends Opaque.String[Password]

  given Iso[UserId, UUID] = Iso[UserId, UUID]

  given key: Key[User, UserId] =
    Key.UUID[User, UserId](_.id)

  override given table: DatabaseTable[User, UserId] =
    DatabaseTable[User, UserId]("users")
