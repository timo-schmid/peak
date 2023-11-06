package ch.timo_schmid.cmf.log.slf4j

import cats.effect.{IO, Resource}
import cats.effect.IO.println
import ch.timo_schmid.cmf.api.LoggerProvider
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j._

object Slf4jLogging:

  given slf4sLoggerProvider: LoggerProvider[IO] =
    (clazz: Class[_]) => Resource.eval(Slf4jFactory[IO].fromClass(clazz))
