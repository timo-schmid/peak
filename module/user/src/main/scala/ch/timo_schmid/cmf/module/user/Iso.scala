package ch.timo_schmid.cmf.module.user

trait Iso[A, B]:

  def from(a: A): B

  def to(b: B): A

object Iso:

  def apply[A, B](using f: <:<[A, B], t: <:<[B, A]): Iso[A, B] =
    new Iso[A, B] {
      override def from(a: A): B = f(a)
      override def to(b: B): A   = t(b)
    }
