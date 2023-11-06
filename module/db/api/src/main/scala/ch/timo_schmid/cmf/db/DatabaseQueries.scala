package ch.timo_schmid.cmf.db

import cats.Id
import doobie.util.fragment.Fragment

trait DatabaseQueries[Key, Data]:

  def select: Fragment

  def byKey(key: Key): Fragment

  def create(data: Data): Fragment

  def update(key: Key, updated: Data): Fragment

  def delete(key: Key): Fragment

object DatabaseQueries:

  def apply[Key, Data](using
      databaseQueries: DatabaseQueries[Key, Data]
  ): DatabaseQueries[Key, Data] =
    databaseQueries
