package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import cats.effect.*
import cats.implicits.*
import ch.timo_schmid.cmf.codec.http4s.circe.CirceHttp4sCodecs
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.core.entity.Key
import ch.timo_schmid.cmf.core.entity.Merge
import org.http4s.EntityEncoder
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class RESTHttp4sHandler[F[_]: Concurrent, Data[_[_]], KeyType](using
    entityKey: Key[Data, KeyType],
    storage: Storage[F, Data, KeyType],
    circeHttp4sCodecs: CirceHttp4sCodecs[F, Data],
    merge: Merge[Data]
):

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import circeHttp4sCodecs.http4sCirceEncoderFull
  import circeHttp4sCodecs.http4sCirceEncoderStreamFull
  import dsl.*

  def list: F[Response[F]] =
    Ok(storage.list)

  def get(key: KeyType): F[Response[F]] =
    storage
      .byKey(key)
      .flatMap {
        case Some(data) => Ok(data)
        case None       => notFound(key)
      }

  def create(data: Data[Id]): F[Response[F]] =
    storage.create(data).flatMap { data =>
      Ok(data)
    }

  def update(key: KeyType)(data: Data[Id]): F[Response[F]] =
    storage
      .update(key, data)
      .flatMap {
        case Some(data) => Ok(data)
        case None       => notFound(key)
      }

  def partialUpdate(key: KeyType)(partial: Data[Option]): F[Response[F]] =
    storage
      .byKey(key)
      .flatMap {
        case Some(full) =>
          storage.update(key, merge.apply(partial, full)).flatMap {
            case Some(data) => Ok(data)
            case None       => notFound(key)
          }
        case None       => notFound(key)
      }

  def delete(key: KeyType): F[Response[F]] =
    storage.delete(key) *> NoContent()

  private def notFound(key: KeyType): F[Response[F]] =
    NotFound(s"Not found: ${entityKey.show.show(key)}")
