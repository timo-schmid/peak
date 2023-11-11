package ch.timo_schmid.cmf.client.http4s

import cats.Id
import cats.effect.kernel.Concurrent
import ch.timo_schmid.cmf.codec.http4s.circe.CirceHttp4sCodecs
import ch.timo_schmid.cmf.core.entity.Key
import org.http4s.*

trait Client[F[_], Data[_[_]], KeyType]:

  def list: F[List[Data[Id]]]

  def get(key: KeyType): F[Option[Data[Id]]]

  def add(data: Data[Id]): F[Data[Id]]

  def update(key: KeyType, data: Data[Id]): F[Data[Id]]

  def patch(key: KeyType, data: Data[Option]): F[Data[Id]]

  def delete(key: KeyType): F[Unit]

object Client:

  def apply[F[_]: Concurrent, Data[_[_]], KeyType](
      http4s: org.http4s.client.Client[F],
      endpointUri: Uri
  )(using codecs: CirceHttp4sCodecs[F, Data], K: Key[Data, KeyType]): Client[F, Data, KeyType] =
    new Client[F, Data, KeyType]:

      override def list: F[List[Data[Id]]] =
        http4s.expect[List[Data[Id]]](request(Method.GET, endpointUri))(
          codecs.http4sCirceDecoderListFull
        )

      override def get(key: KeyType): F[Option[Data[Id]]] =
        http4s.expectOption[Data[Id]](request(Method.GET, endpointUri / K.show.show(key)))(
          codecs.http4sCirceDecoderFull
        )

      override def add(data: Data[Id]): F[Data[Id]] =
        http4s.expect[Data[Id]](
          request(Method.POST, endpointUri)
            .withEntity(data)(codecs.http4sCirceEncoderFull)
        )(
          codecs.http4sCirceDecoderFull
        )

      override def update(key: KeyType, data: Data[Id]): F[Data[Id]] =
        http4s.expect[Data[Id]](
          request(Method.PUT, endpointUri / K.show.show(key))
            .withEntity(data)(codecs.http4sCirceEncoderFull)
        )(
          codecs.http4sCirceDecoderFull
        )

      override def patch(key: KeyType, data: Data[Option]): F[Data[Id]] =
        http4s.expect[Data[Id]](
          request(Method.PATCH, endpointUri / K.show.show(key))
            .withEntity(data)(codecs.http4sCirceEncoderPartial)
        )(
          codecs.http4sCirceDecoderFull
        )

      override def delete(key: KeyType): F[Unit] =
        http4s.expect[Unit](
          request(Method.DELETE, endpointUri / K.show.show(key))
        )

      private def request(method: Method, uri: Uri): Request[F] =
        Request[F](
          method = method,
          uri = uri
        )
