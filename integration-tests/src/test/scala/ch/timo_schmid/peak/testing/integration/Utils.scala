package ch.timo_schmid.peak.testing.integration

import cats.effect.IO
import cats.effect.Resource
import ch.timo_schmid.cmf.client.http4s.Client
import ch.timo_schmid.cmf.module.user.User
import ch.timo_schmid.cmf.module.user.User.UserId
import com.comcast.ip4s.Port
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.http4s.ember.client.EmberClientBuilder

object Utils:

  val DataDirectory = "target/embedded-postgres-data"
  val Port          = 54321

  val embeddedPostgres: Resource[IO, EmbeddedPostgres] =
    Resource.fromAutoCloseable[IO, EmbeddedPostgres](
      IO(
        EmbeddedPostgres
          .builder()
          .setDataDirectory(DataDirectory)
          .setPort(Port)
          .start()
      )
    )

  def userApiClient(port: Port): Resource[IO, Client[IO, User, UserId]] =
    EmberClientBuilder.default[IO].build.map { http4sClient =>
      Client[IO, User, UserId](
        http4sClient,
        org.http4s.Uri.unsafeFromString(s"http://localhost:$port/api/users")
      )
    }
