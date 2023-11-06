package ch.timo_schmid.cmf.cms

import ch.timo_schmid.cmf.cms.BuildInfo
import ch.timo_schmid.cmf.rest.*
import cats.effect.*
import cats.effect.ExitCode.*
import cats.effect.IO.*
import cats.implicits.*
import ch.timo_schmid.cmf.api.*
import ch.timo_schmid.cmf.api.*
import ch.timo_schmid.cmf.log.slf4j.Slf4jLogging.slf4sLoggerProvider

object Main
    extends CatsEffectBootstrap[CmsConfig, NoClients, Http4sRESTService](
      BuildInfo
    ):

  case class Foo[H[_]](bar: H[String], baz: H[Int]) derives ProductK, FunctorK, Merge

  override def run(args: List[String]): IO[ExitCode] =
    for {
      fooIncomplete <- IO.pure(Foo[Option](Some("baz"), None))
      fooComplete   <- IO.pure(Foo[cats.Id]("bar", 123))
      _             <- IO.println(ProductK[Foo].product(fooIncomplete, fooComplete))
      // Merge uses ProductK to combine the two Foos and then uses getOrElse to merge them into one
      _             <- IO.println(Merge[Foo].apply(fooIncomplete, fooComplete))
    } yield ExitCode.Success
