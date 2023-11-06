package ch.timo_schmid.cmf.di

import cats.effect.Resource

trait Injector[F[_], Scope]:

  def scope: Scope

  def inject[Dependency](implicit
      m: Module[F, Scope, Dependency]
  ): Resource[F, Dependency]

object Injector:

  def load[F[_], Scope, Dependency](scope: Scope)(implicit
      m: Module[F, Scope, Dependency]
  ): Resource[F, Dependency] =
    new ScopedInjector[F, Scope](scope).inject[Dependency]
