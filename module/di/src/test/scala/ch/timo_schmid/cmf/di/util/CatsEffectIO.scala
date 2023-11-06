package ch.timo_schmid.cmf.di.util

import cats.effect.IO
import cats.effect.testing.specs2.CatsEffect
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntimeConfig
import cats.effect.unsafe.Scheduler
import java.util.concurrent.Executors
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.AsResult
import org.specs2.execute.Failure
import org.specs2.execute.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

trait CatsEffectIO(ee: ExecutionEnv) extends CatsEffect:

  protected override val Timeout: FiniteDuration = 10.seconds

  private lazy val cachedThreadPool = Executors.newCachedThreadPool()

  implicit def executionContext: ExecutionContext = ExecutionContext.global

  implicit def ioRuntime: IORuntime =
    IORuntime(
      ee.executionContext,
      ExecutionContext.fromExecutor(cachedThreadPool),
      Scheduler.fromScheduledExecutor(ee.scheduledExecutorService),
      () => {
        ee.shutdown()
        cachedThreadPool.shutdownNow()
        ()
      },
      IORuntimeConfig()
    )

  implicit def ioAsResult[R](implicit R: AsResult[R]): AsResult[IO[R]] =
    new AsResult[IO[R]] {
      def asResult(t: => IO[R]): Result =
        t.unsafeRunTimed(Timeout)(ioRuntime)
          .map(R.asResult(_))
          .getOrElse(Failure(s"expectation timed out after $Timeout"))
    }
