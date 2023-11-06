package ch.timo_schmid.cmf.module.user

import ch.timo_schmid.cmf.module.user.Group.GroupId
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import io.circe.*

import java.util.UUID

case class Group(groupId: GroupId, label: String)

object Group:

  given Iso[GroupId, UUID] = Iso[GroupId, UUID]

  opaque type GroupId = UUID
  object GroupId extends Opaque[GroupId, UUID]
