package ch.timo_schmid.cmf.rest

import cats.Id
import cats.data.Tuple2K
import shapeless3.deriving.~>
import shapeless3.deriving.K11

trait Merge[H[_[_]]]:

  def apply(partial: H[Option], full: H[Id]): H[Id]

object Merge:

  inline def apply[H[_[_]]](using mh: Merge[H]): Merge[H] = mh

  given [H[_[_]]](using FunctorK[H], ProductK[H]): Merge[H] with
    def apply(partial: H[Option], full: H[Id]): H[Id] =
      FunctorK[H].mapK(
        ProductK[H].product(partial, full)
      )(mergePartialAndFull)

  val mergePartialAndFull: Tuple2K[Option, Id, *] ~> Id =
    [A] => (elems: Tuple2K[Option, Id, A]) => elems._1.getOrElse[A](elems._2)

  inline def derived[H[_[_]]](using gen: K11.ProductGeneric[H]): Merge[H] =
    given_Merge_H[H]
