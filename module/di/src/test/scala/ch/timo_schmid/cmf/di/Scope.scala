package ch.timo_schmid.cmf.di

enum Scope:

  def fooValue: Int
  def barValue: Int

  case Production(fooValue: Int, barValue: Int)
  case Testing(fooValue: Int, barValue: Int)
