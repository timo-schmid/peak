package ch.timo_schmid.cmf.core.api

import cats.*
import cats.effect.Resource
import cats.implicits.*

trait ClientsProvider[F[_], Config, Clients]:

  def create(config: Config): Resource[F, Clients]

object ClientsProvider:

  given emptyClientsProvider[F[_]: Monad, Clients](using
      loggerProvider: LoggerProvider[F]
  ): ClientsProvider[F, Clients, NoClients] =
    (config: Clients) =>
      for {
        log <- loggerProvider.create(getClass)
        _   <- Resource.eval(log.info("Using empty Clients (NoClients)"))
      } yield NoClients()
