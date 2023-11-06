package ch.timo_schmid.cmf.demo

import cats.effect.*
import cats.effect.IO.*
import ch.timo_schmid.cmf.core.api.NoClients
import ch.timo_schmid.cmf.log.slf4j.Slf4jLogging.slf4sLoggerProvider

object Main
    extends CatsEffectBootstrap[CmsConfig, NoClients, Http4sRESTService](
      BuildInfo
    )
