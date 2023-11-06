package ch.timo_schmid.cmf.rest.http4s

import cats.Applicative
import cats.Show
import cats.Id
import cats.effect.*
import cats.implicits.*
import ch.timo_schmid.cmf.api.Storage
import ch.timo_schmid.cmf.rest.Merge
import ch.timo_schmid.cmf.rest.ToPartial
import fs2.Stream
import org.http4s.Response
import org.http4s.EntityEncoder
import org.http4s.dsl.Http4sDsl
import io.circe.*
import io.circe.generic.auto.*

class RESTHttp4sHandler[F[_]: Concurrent, Key: Show, Data[_[_]]](using
    storage: Storage[F, Key, Data],
    encodeData: EntityEncoder[F, Data[Id]],
    encodeStream: EntityEncoder[F, Stream[F, Data[Id]]],
    toPartial: ToPartial[Data],
    merge: Merge[Data]
):

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl.*

  def list: F[Response[F]] =
    Ok(storage.list)

  def get(key: Key): F[Response[F]] =
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

  def update(key: Key)(data: Data[Id]): F[Response[F]] =
    storage
      .update(key, data)
      .flatMap {
        case Some(data) => Ok(data)
        case None       => notFound(key)
      }

  def partialUpdate(key: Key)(partial: Data[Option]): F[Response[F]] =
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

  def delete(key: Key): F[Response[F]] =
    storage.delete(key) *> NoContent()

  private def notFound(key: Key): F[Response[F]] =
    NotFound(s"Not found: ${key.show}")
