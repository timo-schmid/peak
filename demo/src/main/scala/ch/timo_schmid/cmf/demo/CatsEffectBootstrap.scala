package ch.timo_schmid.cmf.demo

import cats.Show
import cats.effect.*
import cats.effect.IO.*
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.Bootstrap
import ch.timo_schmid.cmf.core.api.Bootstrap.ServiceContext
import ch.timo_schmid.cmf.core.api.LoggerProvider
import org.typelevel.log4cats.Logger
import reflect.Selectable.reflectiveSelectable
import scala.concurrent.duration.*

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
    serverResource(args)
      .use { case (log, _, _, _) =>
        keepServiceRunning(log)
      }

  def serverResource(args: List[String]): Resource[IO, (Logger[IO], Config, Clients, Services)] =
    loggerProvider
      .create(getClass)
      .flatTap(
        info(_)(s"Starting ${buildInfo.name} version ${buildInfo.version}")
      )
      .flatMap { log =>
        for
          config   <- bootstrap.loadConfig(buildInfo.name)
          _        <- info(log)(s"Loaded configuration: ${config.show}")
          clients  <- bootstrap.createClients(config)
          _        <- info(log)(s"Created clients: ${clients.show}")
          services <- bootstrap.createServices(ServiceContext(config, clients, loggerProvider))
          _        <- info(log)(s"Created services: ${services.show}")
        yield (log, config, clients, services)
      }

  private def keepServiceRunning(log: Logger[IO]): IO[ExitCode] =
    (for {
      _         <- log.info(s"Running ${buildInfo.name} version ${buildInfo.version}")
      startTime <- unixTimestamp
      _         <- logRunning(log)(startTime)
    } yield ExitCode.Success)
      .handleErrorWith(errorHandler(log))

  private def logRunning(log: Logger[IO])(startTime: Long): IO[Unit] =
    for
      now <- unixTimestamp
      _   <- log.info(s"Server is up for ${now - startTime} seconds")
      _   <- IO.sleep(1.second)
      _   <- logRunning(log)(startTime)
    yield ()

  private def unixTimestamp: IO[Long] =
    Clock[IO].realTime.map(_.toSeconds)

  private def errorHandler(log: Logger[IO])(error: Throwable): IO[ExitCode] =
    log.error(error)(error.getMessage).as(ExitCode.Error)

  private def info(log: Logger[IO])(message: => String): Resource[IO, Unit] =
    Resource.eval(log.info(message))
