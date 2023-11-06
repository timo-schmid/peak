package ch.timo_schmid.cmf.module.user

import io.circe.*

opaque type Password = String

object Password:

  def apply(password: String): Password = password

  given passwordEncoder: Encoder[Password] =
    Encoder.encodeString.contramap[Password](Password.apply)

  given passwordDecoder: Decoder[Password] =
    Decoder.decodeString.map[Password](Password.apply)
