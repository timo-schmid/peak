package ch.timo_schmid.cmf.core.api

import cats.effect.Resource

trait Bootstrap[F[_], Config, Clients, Services] {

  def loadConfig(namespace: String): Resource[F, Config]

  def createClients(config: Config): Resource[F, Clients]

  def createServices(config: Config): Resource[F, Services]

}

object Bootstrap {

  given composeBootstrap[F[_], Config, Clients, Services](using
      configProvider: ConfigProvider[F, Config],
      clientProvider: ClientsProvider[F, Config, Clients],
      serviceProvider: ServicesProvider[F, Config, Services]
  ): Bootstrap[F, Config, Clients, Services] =
    new Bootstrap[F, Config, Clients, Services]:

      override def loadConfig(namespace: String): Resource[F, Config] =
        configProvider.load(namespace: String)

      override def createClients(config: Config): Resource[F, Clients] =
        clientProvider.create(config)

      override def createServices(config: Config): Resource[F, Services] =
        serviceProvider.create(config)

}
