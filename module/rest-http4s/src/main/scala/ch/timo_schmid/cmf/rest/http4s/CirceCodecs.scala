package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.auto.*
import scala.deriving.Mirror

trait CirceCodecs[Data[_[_]]]:

  given circeDecoderFull: Decoder[Data[Id]]

  given circeEncoderFull: Encoder.AsObject[Data[Id]]

  given circeDecoderPartial: Decoder[Data[Option]]

  given circeEncoderPartial: Encoder.AsObject[Data[Option]]

object CirceCodecs:

  inline def derived[Data[_[_]]](using
      inline mirrorFull: Mirror.Of[Data[Id]],
      mirrorPartial: Mirror.Of[Data[Option]]
  ): CirceCodecs[Data] =
    new CirceCodecs[Data]:

      override given circeDecoderFull: Decoder[Data[Id]] =
        deriveDecoder[Data[Id]].instance

      override given circeEncoderFull: Encoder.AsObject[Data[Id]] =
        deriveEncoder[Data[Id]].instance

      override given circeDecoderPartial: Decoder[Data[Option]] =
        deriveDecoder[Data[Option]].instance

      override given circeEncoderPartial: Encoder.AsObject[Data[Option]] =
        deriveEncoder[Data[Option]].instance
