package ch.timo_schmid.peak.testing.integration

import cats.effect.IO
import cats.effect.Resource
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres

object Utils:

  val DataDirectory = "target/embedded-postgres-data"
  val Port          = 54321

  val embeddedPostgres: Resource[IO, EmbeddedPostgres] =
    Resource.fromAutoCloseable[IO, EmbeddedPostgres](
      IO(
        EmbeddedPostgres
          .builder()
          .setDataDirectory(DataDirectory)
          .setPort(54321)
          // .setLocaleConfig("", "")
          .start()
      )
    )
