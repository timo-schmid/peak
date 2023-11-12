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
    fr0"$SelectAllFields $FromTable"

  override def byKey(key: KeyType): Fragment =
    fr0"$SelectAllFields $FromTable ${whereKey(key)}"

  override def create(data: Data[Id]): Fragment =
    fr0"$InsertIntoTable (${dbFields.fields}) VALUES (${dbFields.values(data)})"

  override def update(key: KeyType, updated: Data[Id]): Fragment =
    fr0"$UpdateTable SET ${dbFields.setValues(updated)} ${whereKey(key)}"

  override def delete(key: KeyType): Fragment =
    fr0"$DeleteFrom ${whereKey(key)}"

  override def fieldNames: List[String] =
    dbFields.fieldNames

  private val TableName: Fragment =
    Fragment.const0(table.name)

  private val SelectAllFields: Fragment =
    fr0"SELECT ${dbFields.fields}"

  private val FromTable: Fragment =
    fr0"FROM $TableName"

  private val InsertIntoTable: Fragment =
    fr0"INSERT INTO $TableName"

  private val UpdateTable: Fragment =
    fr0"UPDATE $TableName"

  private val DeleteFrom: Fragment =
    fr0"DELETE $FromTable"

  private def whereKey(key: KeyType): Fragment =
    fr0" WHERE ID = $key"
