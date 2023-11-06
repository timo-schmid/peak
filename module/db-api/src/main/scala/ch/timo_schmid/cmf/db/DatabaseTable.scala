package ch.timo_schmid.cmf.db

import ch.timo_schmid.cmf.core.entity.Key

trait DatabaseTable[Data[_[_]], KeyType]:

  def name: String

  def key: Key[Data, KeyType]

object DatabaseTable:

  def apply[Data[_[_]], KeyType](tableName: String)(using
      entityKey: Key[Data, KeyType]
  ): DatabaseTable[Data, KeyType] =
    new DatabaseTable[Data, KeyType]:

      override def name: String = tableName

      override def key: Key[Data, KeyType] =
        entityKey: Key[Data, KeyType]
