package ch.timo_schmid.cmf.core.api

import cats.Id
import fs2.Stream

trait Storage[F[_], Data[_[_]], KeyType] {

  def list: Stream[F, Data[Id]]

  def byKey(key: KeyType): F[Option[Data[Id]]]

  def create(data: Data[Id]): F[Data[Id]]

  def update(key: KeyType, updated: Data[Id]): F[Option[Data[Id]]]

  def delete(key: KeyType): F[Unit]

}
