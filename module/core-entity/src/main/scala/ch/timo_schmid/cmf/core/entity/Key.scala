package ch.timo_schmid.cmf.core.entity

import cats.Id
import cats.Show

import java.util.UUID
import scala.util.Try

trait Key[Entity[_[_]], KeyType]:

  def show: Show[KeyType]

  def key(entity: Entity[Id]): KeyType

  def unapply(string: String): Option[KeyType]

object Key:

  def UUID[Data[_[_]], KeyType](_key: Data[Id] => KeyType)(using
      iso: Iso[KeyType, UUID]
  ): Key[Data, KeyType] =
    new Key[Data, KeyType]:

      override def show: Show[KeyType] =
        key => Show.fromToString[java.util.UUID].show(iso.from(key))

      override def key(entity: Data[Id]): KeyType =
        _key(entity)

      override def unapply(string: String): Option[KeyType] =
        Try(java.util.UUID.fromString(string)).toOption.map(iso.to)
