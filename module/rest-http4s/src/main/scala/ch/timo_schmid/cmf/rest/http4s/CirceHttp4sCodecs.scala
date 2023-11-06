package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import cats.effect.Concurrent
import fs2.Stream
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import scala.deriving.Mirror

trait CirceHttp4sCodecs[Data[_[_]]]:

  given http4sCirceDecoderFull[F[_]: Concurrent]: EntityDecoder[F, Data[Id]]

  given http4sCirceDecoderPartial[F[_]: Concurrent]: EntityDecoder[F, Data[Option]]

  given http4sCirceEncoderFull[F[_]: Concurrent]: EntityEncoder.Pure[Data[Id]]

  given http4sCirceEncoderPartial[F[_]: Concurrent]: EntityEncoder.Pure[Data[Option]]

  given http4sCirceEncoderStreamFull[F[_]: Concurrent]: EntityEncoder[F, Stream[F, Data[Id]]]

object CirceHttp4sCodecs:

  inline def derived[Data[_[_]]](using
      inline mirrorFull: Mirror.Of[Data[Id]],
      mirrorPartial: Mirror.Of[Data[Option]]
  ): CirceHttp4sCodecs[Data] =
    new CirceHttp4sCodecs[Data]:

      private lazy val circeCodecs: CirceCodecs[Data] = CirceCodecs.derived

      given http4sCirceDecoderFull[F[_]: Concurrent]: EntityDecoder[F, Data[Id]] =
        circeEntityDecoder[F, Data[Id]](summon[Concurrent[F]], circeCodecs.circeDecoderFull)

      given http4sCirceDecoderPartial[F[_]: Concurrent]: EntityDecoder[F, Data[Option]] =
        circeEntityDecoder[F, Data[Option]](summon[Concurrent[F]], circeCodecs.circeDecoderPartial)

      given http4sCirceEncoderFull[F[_]: Concurrent]: EntityEncoder.Pure[Data[Id]] =
        circeEntityEncoder[Data[Id]](circeCodecs.circeEncoderFull)

      given http4sCirceEncoderPartial[F[_]: Concurrent]: EntityEncoder.Pure[Data[Option]] =
        circeEntityEncoder[Data[Option]](circeCodecs.circeEncoderPartial)

      given http4sCirceEncoderStreamFull[F[_]: Concurrent]: EntityEncoder[F, Stream[F, Data[Id]]] =
        streamJsonArrayEncoderOf[F, Data[Id]](circeCodecs.circeEncoderFull)
