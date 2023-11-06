package ch.timo_schmid.cmf.db

import fs2.Stream

trait Database[F[_], DBIO[_]]:

  def transact[A](dbio: DBIO[A]): F[A]

  def stream[A](stream: Stream[DBIO, A]): Stream[F, A]
