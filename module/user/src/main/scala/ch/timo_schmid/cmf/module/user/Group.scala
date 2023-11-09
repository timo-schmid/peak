package ch.timo_schmid.cmf.module.user

import cats.Id
import ch.timo_schmid.cmf.core.entity.Key
import ch.timo_schmid.cmf.db.DatabaseTable
import ch.timo_schmid.cmf.module.user.Group.GroupId
import ch.timo_schmid.cmf.rest.http4s.REST
import doobie.*
import doobie.postgres.implicits.*
import io.circe.*
import java.util.UUID

case class Group[F[_]](id: F[GroupId], label: F[String]) derives REST

object Group extends AllInstances[Group, GroupId]:

  opaque type GroupId = UUID
  object GroupId extends Opaque[UUID, GroupId]

  import GroupId.opaqueIso

  override given key: Key[Group, GroupId] =
    Key.UUID[Group, GroupId](_.id)

  override given table: DatabaseTable[Group, GroupId] =
    DatabaseTable[Group, GroupId]("groups")
