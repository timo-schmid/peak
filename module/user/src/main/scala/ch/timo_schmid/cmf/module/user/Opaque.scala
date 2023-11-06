package ch.timo_schmid.cmf.module.user

import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import io.circe.*

abstract class Opaque[A, B](using
    get: Get[A],
    put: Put[A],
    encoder: Encoder[A],
    decoder: Decoder[A],
    from: <:<[A, B],
    to: <:<[B, A]
):

  given Iso[A, B]            = Iso[A, B]
  private val iso: Iso[A, B] = summon[Iso[A, B]]
  given Encoder[B]           = encoder.contramap(iso.to)
  given Decoder[B]           = decoder.map(iso.from)
  given Read[B]              = Read.fromGet(get).map(iso.from)
  given Write[B]             = Write.fromPut(put).contramap(iso.to)
