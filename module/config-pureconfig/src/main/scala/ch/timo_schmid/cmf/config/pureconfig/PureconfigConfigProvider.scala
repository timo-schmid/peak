package ch.timo_schmid.cmf.config.pureconfig

import cats.effect.Resource
import cats.effect.kernel.Sync
import ch.timo_schmid.cmf.core.api.ConfigProvider
import scala.deriving.Mirror
import scala.reflect.ClassTag

object PureconfigConfigProvider {
  inline def apply[F[_]: Sync, ConfigType](using
      ClassTag[ConfigType],
      Mirror.Of[ConfigType]
  ): ConfigProvider[F, ConfigType] =
    (namespace: String) =>
      import pureconfig.*
      import pureconfig.generic.derivation.default.*
      import pureconfig.module.catseffect.syntax.*
      given ConfigReader[ConfigType] = ConfigReader.derived[ConfigType]
      Resource.eval[F, ConfigType](ConfigSource.default.at(namespace).loadF[F, ConfigType]())

}
