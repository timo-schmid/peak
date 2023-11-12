package ch.timo_schmid.peak.testing.integration

import cats.Id
import cats.effect.*
import cats.effect.testing.specs2.CatsResource
import ch.timo_schmid.cmf.codec.circe.CirceCodecs
import ch.timo_schmid.cmf.core.entity.Key
import ch.timo_schmid.cmf.core.entity.Merge
import ch.timo_schmid.cmf.demo.CmsConfig
import ch.timo_schmid.cmf.demo.Main
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.core.Fragment
import scala.concurrent.duration.*

abstract class RESTIntegrationTest extends CatsResource[IO, CmsConfig] with SpecificationLike:

  override val Timeout: Duration = 1.minute

  override val resource: Resource[IO, CmsConfig] =
    for {
      _      <- Utils.embeddedPostgres
      config <- runMain
    } yield config

  def testEndpoint[Data[_[_]]: Merge: CirceCodecs, KeyType](endpoint: String)(
      entityInitial: Data[Id],
      update: Data[Id] => Data[Id],
      entityPatch: Data[Option]
  )(using key: Key[Data, KeyType]): Fragment =
    s"Test the endpoint /api/$endpoint" in withResource[MatchResult[_]] { config =>
      Utils
        .http4sClient[Data, KeyType](config.http.port, endpoint)
        .use { client =>
          for
            entityUpdated   <- IO.pure(update(entityInitial))
            entityPatched   <- IO.pure(Merge[Data].apply(entityPatch, entityUpdated))
            listInitial     <- client.list
            afterPost       <- client.add(entityInitial)
            listAfterPost   <- client.list
            afterUpdate     <- client.update(key.key(afterPost), entityUpdated)
            listAfterUpdate <- client.list
            afterPatch      <- client.patch(key.key(afterUpdate), entityPatch)
            finalList       <- client.list
            _               <- client.delete(key.key(afterPatch))
          yield
            listInitial ==== List()
            afterPost ==== entityInitial
            listAfterPost ==== List(entityInitial)
            afterUpdate ==== entityUpdated
            listAfterUpdate ==== List(entityUpdated)
            finalList ==== List(entityPatched)
            afterPatch ==== entityPatched
        }
    }

  private lazy val runMain: Resource[IO, CmsConfig] =
    Main.serverResource(List()).map(_._2)
