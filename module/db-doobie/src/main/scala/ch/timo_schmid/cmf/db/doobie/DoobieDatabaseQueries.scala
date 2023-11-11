package ch.timo_schmid.cmf.db.doobie

import cats.Id
import ch.timo_schmid.cmf.db.DatabaseQueries
import ch.timo_schmid.cmf.db.DatabaseTable
import doobie.*
import doobie.implicits.*
import doobie.util.fragment.Fragment

class DoobieDatabaseQueries[Data[_[_]], KeyType](using
    table: DatabaseTable[Data, KeyType],
    dbFields: DoobieDatabaseFields[Data],
    writeKey: Write[KeyType]
) extends DatabaseQueries[Data[Id], KeyType]:

  override def select: Fragment =
    selectAllFields ++ fromTable

  override def byKey(key: KeyType): Fragment =
    selectAllFields ++ fromTable ++ whereKey(key)

  override def create(data: Data[Id]): Fragment =
    insertIntoTable ++ fr"(" ++ dbFields.fields ++ fr" )" ++
      fr"""values (""" ++ dbFields.values(data) ++ fr" )"

  override def update(key: KeyType, updated: Data[Id]): Fragment =
    updateTable ++
      fr"set" ++
      dbFields.setValues(updated) ++
      whereKey(key)

  override def delete(key: KeyType): Fragment =
    deleteFrom ++ whereKey(key)

  override def fieldNames: List[String] =
    dbFields.fieldNames

  private val tableName: Fragment =
    Fragment.const(table.name)

  private val selectAllFields: Fragment =
    fr"select" ++ dbFields.fields ++ fr""

  private val fromTable: Fragment =
    fr"from" ++ tableName

  private val insertIntoTable: Fragment =
    fr"insert into" ++ tableName

  private val updateTable: Fragment =
    fr"update" ++ tableName

  private val deleteFrom: Fragment =
    fr"delete" ++ fromTable

  private def whereKey(key: KeyType): Fragment =
    fr" where id = $key"
