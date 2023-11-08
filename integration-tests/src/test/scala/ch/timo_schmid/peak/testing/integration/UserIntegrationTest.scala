package ch.timo_schmid.peak.testing.integration

import cats.effect.*
import cats.effect.testing.specs2.CatsEffect
import ch.timo_schmid.cmf.demo.Main
import org.specs2.mutable.Specification
class UserIntegrationTest extends Specification with CatsEffect:

  "The /api/users endpoint" should:

    "return users" in:
      (for {
        _ <- Utils.embeddedPostgres
        _ <- runMain
      } yield ())
        .use { _ =>
          IO(1 === 1)
        }

  private lazy val runMain: Resource[IO, FiberIO[ExitCode]] =
    Resource
      .make(Main.startServerFiber(List()))(_.cancel)
      .flatMap { startServerFiber =>
        Resource.make(
          startServerFiber.join.flatMap {
            case Outcome.Succeeded(serverRunningFiber) => serverRunningFiber
            case Outcome.Errored(e)                    => IO.raiseError(e)
            case Outcome.Canceled()                    => IO.raiseError(new IllegalStateException())
          }
        )(_.cancel)
      }
