package ch.timo_schmid.cmf.rest.http4s

import cats.Id
import cats.effect.Sync
import cats.effect.kernel.Concurrent
import ch.timo_schmid.cmf.codec.http4s.circe.CirceHttp4sCodecs
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.core.entity.Key
import ch.timo_schmid.cmf.core.entity.Merge
import org.http4s.HttpRoutes
import scala.deriving.Mirror
import shapeless3.deriving.K11

trait REST[Data[_[_]]]:

  def routes[F[_]: Sync: Concurrent, KeyType](using
      entityKey: Key[Data, KeyType],
      storage: Storage[F, Data, KeyType],
      codecs: CirceHttp4sCodecs[F, Data]
  ): HttpRoutes[F]

object REST:

  def apply[Data[_[_]]](using REST[Data]): REST[Data] =
    summon[REST[Data]]

  inline def derived[Data[_[_]]](using
      inline mirrorFull: Mirror.Of[Data[Id]],
      inline mirrorPartial: Mirror.Of[Data[Option]],
      gen: K11.Generic[Data],
      genProduct: K11.ProductGeneric[Data]
  ): REST[Data] =
    given Merge[Data] = Merge.derived[Data]
    new REST[Data]:

      override def routes[F[_]: Sync: Concurrent, KeyType](using
          entityKey: Key[Data, KeyType],
          storage: Storage[F, Data, KeyType],
          codecs: CirceHttp4sCodecs[F, Data]
      ): HttpRoutes[F] =
        given RESTHttp4sHandler[F, Data, KeyType] = new RESTHttp4sHandler[F, Data, KeyType]
        new RESTHttp4sRoutes[F, Data, KeyType].routes
