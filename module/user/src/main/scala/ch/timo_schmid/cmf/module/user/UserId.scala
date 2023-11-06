package ch.timo_schmid.cmf.module.user

import io.circe.*
import io.circe.generic.auto.*
import doobie.*
import doobie.postgres.implicits.*

import java.util.UUID
import scala.util.Try

opaque type UserId = UUID

object UserId extends Opaque[UUID, UserId]:

  def apply(userId: UUID): UserId = userId

  def unapply(string: String): Option[UserId] =
    Try(UUID.fromString(string)).toOption
