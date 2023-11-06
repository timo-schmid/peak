package ch.timo_schmid.cmf.di.domain

import cats.effect.IO
import cats.implicits._
import ch.timo_schmid.cmf.di.Module
import ch.timo_schmid.cmf.di.Scope.Production
import ch.timo_schmid.cmf.di.Scope.Testing

trait MainClass:
  def run: IO[Int]

object MainClass:

  given Module[IO, Production, MainClass] =
    Module[IO, Production, MainClass] { injector =>
      for {
        foo <- injector.inject[Foo]
        bar <- injector.inject[Bar]
      } yield new MainClass:
        override def run: IO[Int] = IO.pure(foo.foo + bar.bar)
    }

  given Module[IO, Testing, MainClass] =
    Module[IO, Testing, MainClass] { injector =>
      (injector.inject[Foo], injector.inject[Bar])
        .mapN { case (foo, bar) =>
          new MainClass:
            override def run: IO[Int] = IO.pure(foo.foo * bar.bar)
        }
    }
