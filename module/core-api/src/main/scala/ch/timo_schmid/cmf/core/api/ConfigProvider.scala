package ch.timo_schmid.cmf.core.api

import cats.*
import cats.effect.Resource
import cats.implicits.*

trait ConfigProvider[F[_], Config] extends Provider[F, Unit, Config]:

  def load: Resource[F, Config] =
    create(())

object ConfigProvider:

  given emptyConfigProvider[F[_]: Monad](using
      loggerProvider: LoggerProvider[F]
  ): ConfigProvider[F, NoConfig] =
    _ =>
      for {
        log <- loggerProvider.create(getClass)
        _   <- Resource.eval(log.info("Using empty Config (NoConfig)"))
      } yield NoConfig()