package ch.timo_schmid.cmf.module.user

import cats.Id
import ch.timo_schmid.cmf.db.doobie.DoobieDatabaseFields
import doobie.*
import doobie.implicits.*
import org.specs2.mutable.Specification

import java.util.UUID

class DoobieDatabaseFieldsSpec extends Specification {

  "DoobieDatabaseFields" should {

    "return Fragment for all fields of a user" in {
      DoobieDatabaseFields[
        User
      ].fields.toString ==== """Fragment("userId, login, email")"""
    }

    "return Fragment for all values of a user" in {
      val user =
        User[Id](UserId(UUID.randomUUID()), "foobar", Email("foo", "bar.com"))
      DoobieDatabaseFields[User]
        .setValues(user)
        .toString ==== """Fragment("userId = ?, login = ?, email = ?")"""
    }

    "return Framgment for all values of a user" in {
      val user =
        User[Id](UserId(UUID.randomUUID()), "foobar", Email("foo", "bar.com"))
      DoobieDatabaseFields[User]
        .values(user)
        .toString ==== """Fragment("?, ?, ?")"""
    }

  }

}
