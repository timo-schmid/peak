package ch.timo_schmid.cmf.rest

import cats.data.Tuple2K
import shapeless3.deriving.K11
import shapeless3.deriving.K11.Id

// FIXME: Pretty sure the name is wrong haha
trait ProductK[H[_[_]]]:

  def product[F[_], G[_]](hf: H[F], hg: H[G]): H[Tuple2K[F, G, *]]

object ProductK:

  inline def apply[H[_[_]]](using sh: ProductK[H]): ProductK[H] = sh

  given [T]: ProductK[K11.Id[T]] with
    def product[F[_], G[_]](hf: Id[T][F], hg: Id[T][G]): Tuple2K[F, G, T] =
      Tuple2K[F, G, T](hf, hg)

  given productKGen[H[_[_]]](using
      inst: K11.ProductInstances[ProductK, H]
  ): ProductK[H] with
    def product[A[_], B[_]](ha: H[A], hb: H[B]): H[Tuple2K[A, B, *]] =
      inst.map2[A, B, Tuple2K[A, B, *]](ha, hb)(
        [t[_[_]]] => (s: ProductK[t], ta: t[A], tb: t[B]) => s.product(ta, tb)
      )

  inline def derived[H[_[_]]](using
      K11.ProductInstances[ProductK, H]
  ): ProductK[H] =
    productKGen[H]
