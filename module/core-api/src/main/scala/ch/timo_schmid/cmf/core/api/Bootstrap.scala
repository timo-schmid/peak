package ch.timo_schmid.cmf.core.api

import cats.effect.Resource
import ch.timo_schmid.cmf.core.api.Bootstrap.ServiceContext

trait Bootstrap[F[_], Config, Clients, Services] {

  def loadConfig(namespace: String): Resource[F, Config]

  def createClients(config: Config): Resource[F, Clients]

  def createServices(context: ServiceContext[F, Config, Clients]): Resource[F, Services]

}

object Bootstrap {

  case class ServiceContext[F[_], Config, Clients](
      config: Config,
      clients: Clients,
      loggerProvider: LoggerProvider[F]
  )

  given composeBootstrap[F[_], Config, Clients, Services](using
      configProvider: ConfigProvider[F, Config],
      clientProvider: ClientsProvider[F, Config, Clients],
      serviceProvider: ServicesProvider[F, ServiceContext[F, Config, Clients], Services]
  ): Bootstrap[F, Config, Clients, Services] =
    new Bootstrap[F, Config, Clients, Services]:

      override def loadConfig(namespace: String): Resource[F, Config] =
        configProvider.load(namespace: String)

      override def createClients(config: Config): Resource[F, Clients] =
        clientProvider.create(config)

      override def createServices(
          context: ServiceContext[F, Config, Clients]
      ): Resource[F, Services] =
        serviceProvider.create(context)

}
