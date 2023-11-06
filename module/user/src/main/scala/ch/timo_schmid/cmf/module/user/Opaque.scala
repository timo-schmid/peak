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

  given Iso: entity.Iso[A, B]  = entity.Iso[A, B]
  given Encoder: Encoder[B]    = encoder.contramap(Iso.to)
  given Decoder: Decoder[B]    = decoder.map(Iso.from)
  given Read: doobie.Read[B]   = doobie.Read.fromGet(get).map(Iso.from)
  given Write: doobie.Write[B] = doobie.Write.fromPut(put).contramap(Iso.to)

object Opaque:

  abstract class UUID[A](using
      from: <:<[java.util.UUID, A],
      to: <:<[A, java.util.UUID]
  ) extends Opaque[java.util.UUID, A]:

    def apply(uuid: java.util.UUID): A =
      from(uuid)

    def unapply(string: String): Option[A] =
      Try(java.util.UUID.fromString(string))
        .map(apply)
        .toOption
