package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import cats.effect.kernel.Concurrent
import cats.implicits._
import ch.timo_schmid.cmf.rest
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl

class RESTHttp4sRoutes[F[_]: Concurrent, Key, Data[_[_]]](using
    handler: RESTHttp4sHandler[F, Key, Data],
    entityKey: rest.Key[Data, Key],
    fullDecoder: EntityDecoder[F, Data[Id]],
    partialDecoder: EntityDecoder[F, Data[Option]]
):

  val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import dsl.*

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root                        =>
        handler.list
      case GET -> Root / entityKey(key)       =>
        handler.get(key)
      case r @ POST -> Root                   =>
        r.as[Data[Id]].flatMap(handler.create)
      case r @ PUT -> Root / entityKey(key)   =>
        r.as[Data[Id]].flatMap(handler.update(key))
      case r @ PATCH -> Root / entityKey(key) =>
        r.as[Data[Option]].flatMap(handler.partialUpdate(key))
      case DELETE -> Root / entityKey(key)    =>
        handler.delete(key)
    }

object RESTHttp4sRoutes:

  def apply[F[_]: Concurrent, Key, Data[_[_]]](using
      RESTHttp4sRoutes[F, Key, Data]
  ): HttpRoutes[F] =
    summon[RESTHttp4sRoutes[F, Key, Data]].routes
