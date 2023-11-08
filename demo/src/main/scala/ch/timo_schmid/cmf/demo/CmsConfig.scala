package ch.timo_schmid.cmf.demo

import CmsConfig.*
import cats.Show
import cats.effect.*
import ch.timo_schmid.cmf.config.pureconfig.PureconfigConfigProvider
import ch.timo_schmid.cmf.core.api.ConfigProvider
import ch.timo_schmid.cmf.db.DatabaseConfig
import com.comcast.ip4s.*
import pureconfig.module.ip4s.*

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

  given configProvider[F[_]: Sync]: ConfigProvider[F, CmsConfig] =
    PureconfigConfigProvider[F, CmsConfig]
