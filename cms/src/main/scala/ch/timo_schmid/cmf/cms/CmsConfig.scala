package ch.timo_schmid.cmf.cms

import cats.Show
import cats.effect.*
import com.comcast.ip4s.*
import ch.timo_schmid.cmf.api.ConfigProvider
import ch.timo_schmid.cmf.cms.CmsConfig.Http4s

final case class CmsConfig(http4s: Http4s)

object CmsConfig:

  final case class Http4s(host: Host, port: Port)

  given showCmsConfig: Show[CmsConfig] =
    cmsConfig => cmsConfig.toString()

  given cmsConfigConfigProvider[F[_]: Sync]: ConfigProvider[F, CmsConfig] =
    new ConfigProvider:

      override def create(dependencies: Unit): Resource[F, CmsConfig] =
        Resource.pure[F, CmsConfig](CmsConfig(Http4s(host"0.0.0.0", port"8080")))
