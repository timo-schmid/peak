package ch.timo_schmid.cmf.api

import cats.effect.Resource

trait Provider[F[_], Dependencies, Provided] {

  def create(dependencies: Dependencies): Resource[F, Provided]

}
