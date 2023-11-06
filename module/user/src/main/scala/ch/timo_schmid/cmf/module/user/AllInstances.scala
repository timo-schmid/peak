package ch.timo_schmid.cmf.module.user

import cats.Id
import cats.effect.*
import ch.timo_schmid.cmf.core.api.Storage
import ch.timo_schmid.cmf.core.entity.*
import ch.timo_schmid.cmf.db.Database
import ch.timo_schmid.cmf.db.DatabaseQueries
import ch.timo_schmid.cmf.db.DatabaseTable
import ch.timo_schmid.cmf.db.doobie.*
import ch.timo_schmid.cmf.module.user.AllInstances.*
import ch.timo_schmid.cmf.rest.*
import ch.timo_schmid.cmf.rest.http4s.*
import doobie.Read
import doobie.Write
import doobie.free.ConnectionIO

trait AllInstances[Data[F[_]], KeyType]
    extends DoobieStorageInstances[KeyType, Data]
    with RESTHttp4sInstances[KeyType, Data]:

  type Full    = Data[Id]
  type Partial = Data[Option]

  given table: DatabaseTable[Data, KeyType]

  given key: Key[Data, KeyType]

object AllInstances:

  trait RESTHttp4sInstances[KeyType, Data[_[_]]]:

    given restHttp4sHandler[F[_]: Concurrent](using
        Key[Data, KeyType],
        Storage[F, Data, KeyType],
        CirceHttp4sCodecs[Data],
        ToPartial[Data],
        Merge[Data]
    ): RESTHttp4sHandler[F, Data, KeyType] =
      new RESTHttp4sHandler[F, Data, KeyType]

    given restHttp4sRoutes[F[_]: Async: Sync](using
        RESTHttp4sHandler[F, Data, KeyType],
        Key[Data, KeyType],
        Database[F, ConnectionIO],
        CirceHttp4sCodecs[Data],
        ToPartial[Data],
        Merge[Data]
    ): RESTHttp4sRoutes[F, Data, KeyType] =
      new RESTHttp4sRoutes[F, Data, KeyType]

  trait DoobieStorageInstances[KeyType, Data[_[_]]]:

    given doobieDatabaseQueries(using
        table: DatabaseTable[Data, KeyType],
        dbFields: DoobieDatabaseFields[Data],
        writeKey: Write[KeyType]
    ): DatabaseQueries[Data[Id], KeyType] =
      new DoobieDatabaseQueries[Data, KeyType]

    given doobieStorage[F[_]: Sync](using
        database: Database[F, ConnectionIO],
        databaseQueries: DatabaseQueries[Data[Id], KeyType],
        writeKey: Write[KeyType],
        readData: Read[Data[Id]],
        writeData: Write[Data[Id]]
    ): Storage[F, Data, KeyType] =
      new DoobieStorage[F, Data, KeyType]
