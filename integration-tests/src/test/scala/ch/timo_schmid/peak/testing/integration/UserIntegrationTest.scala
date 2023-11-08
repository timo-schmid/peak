package ch.timo_schmid.peak.testing.integration

import cats.effect.*
import cats.effect.testing.specs2.CatsEffect
import cats.implicits.*
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

  private lazy val runMain: Resource[IO, Unit] =
    Main.serverResource(List()).void
