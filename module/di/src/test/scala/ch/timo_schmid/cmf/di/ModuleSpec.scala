package ch.timo_schmid.cmf.di

import Scope.*
import cats.effect.IO
import ch.timo_schmid.cmf.di.domain.MainClass
import ch.timo_schmid.cmf.di.util.CatsEffectIO
import org.scalacheck.*
import org.specs2.ScalaCheck
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import org.specs2.scalacheck.ScalaCheckFunction2

class ModuleSpec(ee: ExecutionEnv) extends Specification with CatsEffectIO(ee) with ScalaCheck:

  "A Module should" >> {

    "load the MainClass for the Production scope" >>
      runTest[Production](Production.apply)(_ + _)

    "load the MainClass for the Testing scope" >>
      runTest[Testing](Testing.apply)(_ * _)

  }

  private def runTest[Scope](scope: (Int, Int) => Scope)(op: (Int, Int) => Int)(implicit
      main: Module[IO, Scope, MainClass]
  ): ScalaCheckFunction2[Int, Int, IO[MatchResult[Int]]] =
    prop[Int, Int, IO[MatchResult[Int]]] { (foo: Int, bar: Int) =>
      Injector
        .load[IO, Scope, MainClass](scope(foo, bar))
        .use { mainClass =>
          mainClass.run
        }
        .map(_ ==== op(foo, bar))
    }
