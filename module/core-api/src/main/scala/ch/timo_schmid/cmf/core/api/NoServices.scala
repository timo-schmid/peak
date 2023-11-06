package ch.timo_schmid.cmf.core.api

import cats.Show

opaque type NoServices = Unit

object NoServices:
  def apply(): NoServices = ()

  given showNoServices: Show[NoServices] =
    _ => "NoServices"
