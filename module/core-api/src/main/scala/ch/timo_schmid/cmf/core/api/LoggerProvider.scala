package ch.timo_schmid.cmf.core.api

import org.typelevel.log4cats.Logger

trait LoggerProvider[F[_]] extends Provider[F, Class[_], Logger[F]]
