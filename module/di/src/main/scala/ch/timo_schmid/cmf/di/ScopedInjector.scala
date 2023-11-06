package ch.timo_schmid.cmf.di

import cats.effect.*

private[di] class ScopedInjector[F[_], Scope](_scope: Scope) extends Injector[F, Scope]:

  override def scope: Scope = _scope

  override def inject[Dependency](implicit
      m: Module[F, Scope, Dependency]
  ): Resource[F, Dependency] =
    m.load(this)
