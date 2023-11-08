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
    startServerFiber(args)
      .flatMap(_.join)
      .flatMap {
        case Outcome.Succeeded(mainLoopFiber) =>
          mainLoopFiber
            .flatMap(_.join)
            .flatMap {
              case Outcome.Succeeded(exitCode) =>
                exitCode
              case Outcome.Errored(e)          =>
                IO.println(s"Main loop: Error: ${e.getMessage}") *> IO.pure(ExitCode.Error)
              case Outcome.Canceled()          =>
                IO.println(s"Cancelled") *> IO.pure(ExitCode.Error)
            }
        case Outcome.Errored(e)               =>
          IO.println(s"Error: ${e.getMessage}") *> IO.pure(ExitCode.Error)
        case Outcome.Canceled()               =>
          IO.println(s"Cancelled") *> IO.pure(ExitCode.Error)
      }

  def startServerFiber(args: List[String]): IO[FiberIO[FiberIO[ExitCode]]] =
    loggerProvider
      .create(getClass)
      .flatTap(
        info(_)(s"Starting ${buildInfo.name} version ${buildInfo.version}")
      )
      .flatMap { log =>
        for {
          config   <- bootstrap.loadConfig(buildInfo.name)
          _        <- info(log)(s"Loaded configuration: ${config.show}")
          clients  <- bootstrap.createClients(config)
          _        <- info(log)(s"Created clients: ${clients.show}")
          services <- bootstrap.createServices(config)
          _        <- info(log)(s"Created services: ${services.show}")
        } yield (log, config, clients, services)
      }
      .use { case (log, _, _, _) =>
        serviceRunningFiber(log)
      }
      .start

  private def serviceRunningFiber(log: Logger[IO]): IO[FiberIO[ExitCode]] =
    runService(log)
      .handleErrorWith(errorHandler(log))

  private def runService(log: Logger[IO]): IO[FiberIO[ExitCode]] =
    (for {
      _ <- log.info(s"Running ${buildInfo.name} version ${buildInfo.version}")
      _ <- never
    } yield ExitCode.Success).start

  private def errorHandler(log: Logger[IO])(error: Throwable): IO[FiberIO[ExitCode]] =
    log.info(error.getMessage) *>
      pure(ExitCode.Error).start

  private def info(log: Logger[IO])(message: => String): Resource[IO, Unit] =
    Resource.eval(log.info(message))
