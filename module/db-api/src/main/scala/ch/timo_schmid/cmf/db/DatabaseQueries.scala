package ch.timo_schmid.cmf.db

import doobie.util.fragment.Fragment

trait DatabaseQueries[Data, KeyType]:

  def select: Fragment

  def byKey(key: KeyType): Fragment

  def create(data: Data): Fragment

  def update(key: KeyType, updated: Data): Fragment

  def delete(key: KeyType): Fragment

  def fieldNames: List[String]

object DatabaseQueries:

  def apply[Data, KeyType](using
      databaseQueries: DatabaseQueries[Data, KeyType]
  ): DatabaseQueries[Data, KeyType] =
    databaseQueries
