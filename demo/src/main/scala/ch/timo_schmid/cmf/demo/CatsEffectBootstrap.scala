package ch.timo_schmid.cmf.demo

import cats.Show
import cats.effect.*
import cats.effect.ExitCode.Success
import cats.effect.IO.*
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.Bootstrap
import ch.timo_schmid.cmf.core.api.LoggerProvider
import org.typelevel.log4cats.Logger
import reflect.Selectable.reflectiveSelectable

abstract class CatsEffectBootstrap[Config: Show, Clients: Show, Services: Show](
    buildInfo: Any {
      val name: String
      val version: String
    }
)(using
    bootstrap: Bootstrap[IO, Config, Clients, Services],
    loggerProvider: LoggerProvider[IO]
) extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =
    loggerProvider
      .create(getClass)
      .flatTap(
        info(_)(s"Starting ${buildInfo.name} version ${buildInfo.version}")
      )
      .flatMap { log =>
        for {
          config   <- bootstrap.loadConfig
          _        <- info(log)(s"Loaded configuration: ${config.show}")
          clients  <- bootstrap.createClients(config)
          _        <- info(log)(s"Created clients: ${clients.show}")
          services <- bootstrap.createServices(config)
          _        <- info(log)(s"Created services: ${services.show}")
        } yield (log, config, clients, services)
      }
      .use { case (log, config, clients, services) =>
        runService(config, clients, services)(log)
          .handleErrorWith(errorHandler(log))
      }

  def runService(
      config: Config,
      clients: Clients,
      services: Services
  )(log: Logger[IO]): IO[ExitCode] =
    log.info(s"Running ${buildInfo.name} version ${buildInfo.version}") *>
      never *>
      ExitCode.Success.pure[IO]

  def errorHandler(log: Logger[IO])(error: Throwable): IO[ExitCode] =
    log.info(error.getMessage) *>
      pure(ExitCode.Error)

  private def info(log: Logger[IO])(message: => String): Resource[IO, Unit] =
    Resource.eval(log.info(message))
