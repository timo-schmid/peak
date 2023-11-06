package ch.timo_schmid.cmf.db

import cats.effect.Resource

trait DatabaseConnection[F[_], DBIO[_]] {

  def resource: Resource[F, Database[F, DBIO]]

}
