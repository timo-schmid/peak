package ch.timo_schmid.cmf.di

import cats.effect.IO
import cats.effect.Resource

trait Module[F[_], Scope, Dependency]:

  def load(injector: Injector[F, Scope]): Resource[F, Dependency]

object Module:

  def apply[F[_], Scope, Dependency](
      f: Injector[F, Scope] => Resource[F, Dependency]
  ): Module[F, Scope, Dependency] =
    (injector: Injector[F, Scope]) => f(injector)

  def forScope[F[_], Scope, Dependency](
      f: Scope => Dependency
  ): Module[F, Scope, Dependency] =
    apply[F, Scope, Dependency] { injector =>
      Resource.pure[F, Dependency](f(injector.scope))
    }
