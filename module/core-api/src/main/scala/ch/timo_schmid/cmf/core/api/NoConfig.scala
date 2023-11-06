package ch.timo_schmid.cmf.core.api

import cats.Show

opaque type NoConfig = Unit

object NoConfig:

  def apply(): NoConfig = ()

  given showNoConfig: Show[NoConfig] =
    _ => "NoConfig"
