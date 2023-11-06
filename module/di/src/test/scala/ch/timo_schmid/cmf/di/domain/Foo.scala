package ch.timo_schmid.cmf.di.domain

import cats.effect.IO
import ch.timo_schmid.cmf.di.Module
import ch.timo_schmid.cmf.di.Scope
import ch.timo_schmid.cmf.di.Scope.Production
import ch.timo_schmid.cmf.di.Scope.Testing

class Foo(scope: Scope):
  def foo: Int = scope.fooValue

object Foo:

  given Module[IO, Production, Foo] =
    Module.forScope[IO, Production, Foo](Foo(_))

  given Module[IO, Testing, Foo] =
    Module.forScope[IO, Testing, Foo](Foo(_))
