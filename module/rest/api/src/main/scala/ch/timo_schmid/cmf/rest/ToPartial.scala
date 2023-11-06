package ch.timo_schmid.cmf.rest

import cats.Id

trait ToPartial[Data[_[_]]] {
  def apply(full: Data[Id]): Data[Option]
}
