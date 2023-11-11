package ch.timo_schmid.peak.testing.integration

import cats.Id
import cats.effect.*
import cats.effect.testing.specs2.CatsEffect
import cats.implicits.*
import ch.timo_schmid.cmf.core.entity.Merge
import ch.timo_schmid.cmf.demo.CmsConfig
import ch.timo_schmid.cmf.demo.Main
import ch.timo_schmid.cmf.module.user.Email
import ch.timo_schmid.cmf.module.user.User
import ch.timo_schmid.cmf.module.user.User.UserId
import org.specs2.mutable.Specification
import scala.concurrent.duration.*

class UserIntegrationTest extends Specification with CatsEffect:

  override val Timeout: Duration = 1.minute

  private val user        = User[Id](UserId.random(), "timo", Email("foo", "bar.com"))
  private val userUpdated = user.copy(email = Email("timo", "schmid.ch"))
  private val userPatch   = User[Option](None, Some("timo.schmid"), None)
  private val userPatched = Merge[User].apply(userPatch, userUpdated)

  "The /api/users endpoint" should:

    "return users" in:
      (for {
        _             <- Utils.embeddedPostgres
        config        <- runMain
        userApiClient <- Utils.userApiClient(config.http.port)
      } yield userApiClient)
        .use { userApiClient =>
          for {
            initialList <- userApiClient.list
            afterPost   <- userApiClient.add(user)
            afterUpdate <- userApiClient.update(afterPost.id, userUpdated)
            afterPatch  <- userApiClient.patch(afterUpdate.id, userPatch)
            _           <- userApiClient.delete(afterPatch.id)
          } yield {
            initialList ==== List()
            afterPost ==== user
            afterUpdate ==== userUpdated
            afterPatch ==== userPatched
          }
        }

  private lazy val runMain: Resource[IO, CmsConfig] =
    Main.serverResource(List()).map(_._2)
