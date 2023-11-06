package ch.timo_schmid.cmf.demo

import CmsConfig.*
import cats.Show
import cats.effect.*
import ch.timo_schmid.cmf.core.api.ConfigProvider
import ch.timo_schmid.cmf.db.DatabaseConfig
import com.comcast.ip4s.*

final case class CmsConfig(db: Db, http: Http)

object CmsConfig:

  final case class Http(host: Host, port: Port)

  final case class Db(
      override val numThreads: Int,
      override val host: Host,
      override val port: Port,
      override val username: String,
      override val password: String,
      override val database: String
  ) extends DatabaseConfig

  given showCmsConfig: Show[CmsConfig] =
    Show.fromToString[CmsConfig]

  given cmsConfigConfigProvider[F[_]: Sync]: ConfigProvider[F, CmsConfig] =
    (dependencies: Unit) =>
      Resource.pure[F, CmsConfig](
        CmsConfig(
          Db(32, host"127.0.0.1", port"5432", "hexagons", "b7L5451RReG1", "hexagons"),
          Http(host"0.0.0.0", port"8080")
        )
      )
