package ch.timo_schmid.cmf.core.api

import cats.Show

opaque type NoClients = Unit

object NoClients:
  def apply(): NoClients = ()

  given showNoClients: Show[NoClients] =
    _ => "NoClients"
