package ch.timo_schmid.cmf.db.doobie

import cats.Id
import ch.timo_schmid.cmf.db.DatabaseQueries
import ch.timo_schmid.cmf.db.doobie.DoobieDatabaseFields
import doobie.*
import doobie.implicits.*
import doobie.util.fragment.Fragment

class DoobieDatabaseQueries[Key, H[_[_]]](using
                                          dbFields: DoobieDatabaseFields[H],
                                          writeKey: Write[Key]
) extends DatabaseQueries[Key, H[Id]]:

  override def select: Fragment =
    selectAllFields ++ fromTable

  override def byKey(key: Key): Fragment =
    selectAllFields ++ fromTable ++ whereKey(key)

  override def create(data: H[Id]): Fragment =
    insertIntoTable ++ dbFields.fields ++
      fr"""values""" ++ dbFields.values(data)

  override def update(key: Key, updated: H[Id]): Fragment =
    updateTable ++
      fr"set" ++
      dbFields.setValues(updated) ++
      whereKey(key)

  override def delete(key: Key): Fragment =
    deleteFrom ++ whereKey(key)

  private val tableName: Fragment =
    fr"users"

  private val selectAllFields: Fragment =
    fr"select" ++ dbFields.fields

  private val fromTable: Fragment =
    fr"from" ++ tableName

  private val insertIntoTable: Fragment =
    fr"insert into" ++ tableName

  private val updateTable: Fragment =
    fr"update" ++ tableName

  private val deleteFrom: Fragment =
    fr"delete" ++ fromTable

  private def whereKey(key: Key): Fragment =
    fr"where id = $key"
