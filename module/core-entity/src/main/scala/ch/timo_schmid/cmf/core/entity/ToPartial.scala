package ch.timo_schmid.cmf.core.entity

import cats.Id
import shapeless3.deriving.K11

trait ToPartial[Data[_[_]]]:

  def apply(full: Data[Id]): Data[Option]

object ToPartial:

  inline def derived[Data[_[_]]](using gen: K11.Generic[Data]): ToPartial[Data] =
    (full: Data[Id]) =>
      FunctorK
        .derived[Data]
        .mapK[Id, Option](full)([A] => (a: Id[A]) => Some[A](a))
