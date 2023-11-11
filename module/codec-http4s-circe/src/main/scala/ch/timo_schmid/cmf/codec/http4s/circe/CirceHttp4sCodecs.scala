package ch.timo_schmid.cmf.codec.http4s.circe

import cats.Id
import cats.effect.Concurrent
import ch.timo_schmid.cmf.codec.circe.CirceCodecs
import fs2.RaiseThrowable
import fs2.Stream
import io.circe.Decoder.decodeList
import io.circe.fs2.decoder
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*

trait CirceHttp4sCodecs[F[_], Data[_[_]]]:

  given http4sCirceDecoderFull: EntityDecoder[F, Data[Id]]

  given http4sCirceDecoderPartial: EntityDecoder[F, Data[Option]]

  given http4sCirceEncoderFull: EntityEncoder.Pure[Data[Id]]

  given http4sCirceEncoderPartial: EntityEncoder.Pure[Data[Option]]

  given http4sCirceEncoderStreamFull: EntityEncoder[F, Stream[F, Data[Id]]]

  given http4sCirceDecoderStreamFull: EntityDecoder[F, Stream[F, Data[Id]]]

  given http4sCirceDecoderListFull: EntityDecoder[F, List[Data[Id]]]

object CirceHttp4sCodecs:

  given forConcurrent[F[_]: Concurrent, Data[_[_]]](using
      circeCodecs: CirceCodecs[Data]
  ): CirceHttp4sCodecs[F, Data] =
    new CirceHttp4sCodecs[F, Data]:

      override given http4sCirceDecoderFull: EntityDecoder[F, Data[Id]] =
        circeEntityDecoder[F, Data[Id]](summon[Concurrent[F]], circeCodecs.circeDecoderFull)

      override given http4sCirceDecoderPartial: EntityDecoder[F, Data[Option]] =
        circeEntityDecoder[F, Data[Option]](summon[Concurrent[F]], circeCodecs.circeDecoderPartial)

      override given http4sCirceEncoderFull: EntityEncoder.Pure[Data[Id]] =
        circeEntityEncoder[Data[Id]](circeCodecs.circeEncoderFull)

      override given http4sCirceEncoderPartial: EntityEncoder.Pure[Data[Option]] =
        circeEntityEncoder[Data[Option]](circeCodecs.circeEncoderPartial)

      override given http4sCirceEncoderStreamFull: EntityEncoder[F, Stream[F, Data[Id]]] =
        streamJsonArrayEncoderOf[F, Data[Id]](circeCodecs.circeEncoderFull)

      override given http4sCirceDecoderStreamFull: EntityDecoder[F, Stream[F, Data[Id]]] =
        streamJsonArrayDecoder[F]
          .map(
            decoder[F, Data[Id]](
              RaiseThrowable.fromApplicativeError,
              circeCodecs.circeDecoderFull
            )
          )

      override given http4sCirceDecoderListFull: EntityDecoder[F, List[Data[Id]]] =
        circeEntityDecoder[F, List[Data[Id]]](
          summon[Concurrent[F]],
          decodeList[Data[Id]](circeCodecs.circeDecoderFull)
        )
