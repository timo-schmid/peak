package ch.timo_schmid.cmf.api

import cats.*
import cats.effect.Resource
import cats.implicits.*

trait ServicesProvider[F[_], Config, Services]:
  def create(config: Config): Resource[F, Services]

object ServicesProvider:

  given emptyServiceProvider[F[_]: Monad, Config](using
      loggerProvider: LoggerProvider[F]
  ): ServicesProvider[F, Config, NoServices] =
    (config: Config) =>
      for {
        log <- loggerProvider.create(getClass)
        _   <- Resource.eval(log.info("Using empty Services (NoServices)"))
      } yield NoServices()
