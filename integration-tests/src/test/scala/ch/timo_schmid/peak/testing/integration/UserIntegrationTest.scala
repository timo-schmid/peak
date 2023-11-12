package ch.timo_schmid.peak.testing.integration

import cats.Id
import cats.implicits.*
import ch.timo_schmid.cmf.core.entity.Merge
import ch.timo_schmid.cmf.module.user.*
import ch.timo_schmid.cmf.module.user.Group.GroupId
import ch.timo_schmid.cmf.module.user.User.UserId

class UserIntegrationTest extends RESTIntegrationTest:

  s"The REST endpoints" should:

    testEndpoint[Group, GroupId]("groups")(
      Group[Id](GroupId.random(), "admins"),
      _.copy(label = "Admins"),
      Group[Option](None, Some("Administrators"))
    )

    testEndpoint[User, UserId]("users")(
      User[Id](UserId.random(), "timo", Email("foo", "bar.com")),
      _.copy(email = Email("timo", "schmid.ch")),
      User[Option](None, Some("timo.schmid"), None)
    )
