package ch.timo_schmid.cmf.api

import cats.Id
import fs2.Stream

trait Storage[F[_], Key, Data[_[_]]] {

  def list: Stream[F, Data[Id]]

  def byKey(key: Key): F[Option[Data[Id]]]

  def create(data: Data[Id]): F[Data[Id]]

  def update(key: Key, updated: Data[Id]): F[Option[Data[Id]]]

  def delete(key: Key): F[Unit]

}
