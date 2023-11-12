package ch.timo_schmid.cmf.demo

import cats.effect.Async
import ch.timo_schmid.cmf.core.api.LoggerProvider
import ch.timo_schmid.cmf.core.api.Provider
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.db.DatabaseConfig
import ch.timo_schmid.cmf.db.doobie.databaseConnection
import ch.timo_schmid.cmf.module.user.Group
import ch.timo_schmid.cmf.module.user.Group.GroupId
import ch.timo_schmid.cmf.module.user.User
import ch.timo_schmid.cmf.module.user.User.UserId

case class Storages[F[_]](
    group: Storage[F, Group, GroupId],
    user: Storage[F, User, UserId]
)

object Storages:

  case class Params[F[_]](config: DatabaseConfig, loggerProvider: LoggerProvider[F])

  def provider[F[_]: Async]: Provider[F, Params[F], Storages[F]] =
    params =>
      given DatabaseConfig    = params.config
      given LoggerProvider[F] = params.loggerProvider
      databaseConnection[F].resource.flatMap { connection =>
        for
          group <- Group.doobieStorageProvider.create(connection)
          user  <- User.doobieStorageProvider.create(connection)
        yield Storages[F](group, user)
      }
