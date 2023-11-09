package ch.timo_schmid.cmf.module.user

import ch.timo_schmid.cmf.core.entity
import doobie.postgres.implicits.*
import io.circe.*
import scala.util.Try

abstract class Opaque[A, B](using
    get: doobie.Get[A],
    put: doobie.Put[A],
    encoder: Encoder[A],
    decoder: Decoder[A],
    from: <:<[A, B],
    to: <:<[B, A]
):

  given opaqueIso: entity.Iso[A, B]  = entity.Iso[A, B]
  given opaqueEncoder: Encoder[B]    = encoder.contramap(opaqueIso.to)
  given opaqueDecoder: Decoder[B]    = decoder.map(opaqueIso.from)
  given opaqueRead: doobie.Read[B]   = doobie.Read.fromGet(get).map(opaqueIso.from)
  given opaqueWrite: doobie.Write[B] = doobie.Write.fromPut(put).contramap(opaqueIso.to)

object Opaque:

  abstract class String[A](using from: <:<[java.lang.String, A], to: <:<[A, java.lang.String])
      extends Opaque[java.lang.String, A]:

    def apply(password: java.lang.String): A = password

    given encoder: Encoder[A] =
      Encoder.encodeString.contramap[A](apply)

    given decoder: Decoder[A] =
      Decoder.decodeString.map[A](apply)

  abstract class UUID[A](using
      from: <:<[java.util.UUID, A],
      to: <:<[A, java.util.UUID]
  ) extends Opaque[java.util.UUID, A]:

    def apply(uuid: java.util.UUID): A =
      from(uuid)

    def unapply(string: java.lang.String): Option[A] =
      Try(java.util.UUID.fromString(string))
        .map(apply)
        .toOption
