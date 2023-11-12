package ch.timo_schmid.peak.testing.integration

import cats.effect.IO
import cats.effect.Resource
import ch.timo_schmid.cmf.client.http4s.Client
import ch.timo_schmid.cmf.codec.circe.CirceCodecs
import ch.timo_schmid.cmf.core.entity.Key
import com.comcast.ip4s.Port
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.http4s.Uri
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

  def http4sClient[Data[_[_]], KeyType](port: Port, endpoint: String)(using
      CirceCodecs[Data],
      Key[Data, KeyType]
  ): Resource[IO, Client[IO, Data, KeyType]] =
    EmberClientBuilder.default[IO].build.map { http4sClient =>
      Client[IO, Data, KeyType](
        http4sClient,
        Uri.unsafeFromString(s"http://localhost:$port/api/$endpoint")
      )
    }
