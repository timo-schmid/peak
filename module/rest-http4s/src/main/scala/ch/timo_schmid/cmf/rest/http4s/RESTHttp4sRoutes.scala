package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import cats.effect.Concurrent
import cats.implicits.*
import ch.timo_schmid.cmf.codec.http4s.circe.CirceHttp4sCodecs
import ch.timo_schmid.cmf.core.entity.Key
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class RESTHttp4sRoutes[F[_]: Concurrent, Data[_[_]], KeyType](using
    handler: RESTHttp4sHandler[F, Data, KeyType],
    entityKey: Key[Data, KeyType],
    circeHttp4sCodecs: CirceHttp4sCodecs[F, Data]
):

  private val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
  import circeHttp4sCodecs.http4sCirceDecoderFull
  import circeHttp4sCodecs.http4sCirceDecoderPartial
  import dsl.*

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root                        =>
        handler.list.handleErrorWith(errorHandler)
      case GET -> Root / entityKey(key)       =>
        handler.get(key).handleErrorWith(errorHandler)
      case r @ POST -> Root                   =>
        r.as[Data[Id]].flatMap(handler.create).handleErrorWith(errorHandler)
      case r @ PUT -> Root / entityKey(key)   =>
        r.as[Data[Id]].flatMap(handler.update(key)).handleErrorWith(errorHandler)
      case r @ PATCH -> Root / entityKey(key) =>
        r.as[Data[Option]].flatMap(handler.partialUpdate(key)).handleErrorWith(errorHandler)
      case DELETE -> Root / entityKey(key)    =>
        handler.delete(key).handleErrorWith(errorHandler)
    }

  private def errorHandler[A](t: Throwable): F[A] =
    Concurrent[F].raiseError {
      println(t.getMessage)
      t.printStackTrace()
      t
    }

object RESTHttp4sRoutes:

  def apply[F[_]: Concurrent, KeyType, Data[_[_]]](using
      RESTHttp4sRoutes[F, Data, KeyType]
  ): HttpRoutes[F] =
    summon[RESTHttp4sRoutes[F, Data, KeyType]].routes
