package ch.timo_schmid.cmf.di.domain

import cats.effect.IO
import ch.timo_schmid.cmf.di.Module
import ch.timo_schmid.cmf.di.Scope
import ch.timo_schmid.cmf.di.Scope.Production
import ch.timo_schmid.cmf.di.Scope.Testing

class Bar(scope: Scope):
  def bar: Int =
    scope.barValue

object Bar:

  given Module[IO, Production, Bar] =
    Module.forScope[IO, Production, Bar](Bar(_))

  given Module[IO, Testing, Bar] =
    Module.forScope[IO, Testing, Bar](Bar(_))
