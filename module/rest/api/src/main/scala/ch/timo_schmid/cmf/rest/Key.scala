package ch.timo_schmid.cmf.rest

import cats.Id

trait Key[Entity[_[_]], KeyType] {

  def key(entity: Entity[Id]): KeyType

  def unapply(string: String): Option[KeyType]

}
