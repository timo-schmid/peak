package ch.timo_schmid.cmf.di.domain

import cats.effect.IO
import cats.effect.kernel.Resource
import ch.timo_schmid.cmf.di.Scope.{Production, Testing}
import ch.timo_schmid.cmf.di.{Module, Scope}

class Foo(scope: Scope):
  def foo: Int = scope.fooValue

object Foo:

  given Module[IO, Production, Foo] =
    Module.forScope[IO, Production, Foo](Foo(_))

  given Module[IO, Testing, Foo] =
    Module.forScope[IO, Testing, Foo](Foo(_))
