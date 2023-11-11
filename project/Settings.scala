import Build.SbtModules
import Build.SbtSettings
import sbt.Keys.*
import sbtbuildinfo.BuildInfoKeys.*

object Settings {

  val Demo: SbtSettings = Common()

  val IntegrationTests: SbtSettings = Common(Dependencies.IntegrationTests)

  object Module {
    object Client {
      val Http4s: SbtSettings = Common(Dependencies.Module.Client)
    }
    object Codec  {
      val Circe: SbtSettings = Common(Dependencies.Module.Codec.Circe)
      val Http4sCirce: SbtSettings = Common(Dependencies.Module.Codec.Http4sCirce)
    }
    object Config {
      val Pureconfig: SbtSettings = Common(Dependencies.Module.Config.Pureconfig)
    }
    object Core   {
      val Api: SbtSettings    = Common(Dependencies.Module.Core.Api)
      val Entity: SbtSettings = Common(Dependencies.Module.Core.Entity)
    }
    object Db     {
      val Api: SbtSettings    = Common(Dependencies.Module.Db.Api)
      val Doobie: SbtSettings = Common(Dependencies.Module.Db.Doobie)
    }
    val Di: SbtSettings = Common(Dependencies.Module.Di)
    object Log    {
      val Slf4j: SbtSettings = Common(Dependencies.Module.Log.Slf4j)
    }
    val User: SbtSettings = Common(Dependencies.Module.Di)
    object Rest   {
      val Api: SbtSettings    = Common(Dependencies.Module.Rest.Api)
      val Http4s: SbtSettings = Common(Dependencies.Module.Rest.Http4s)
    }

  }

  val Root: SbtSettings = Common()

  def Common(dependencies: SbtModules = Seq.empty): SbtSettings =
    Seq(
      version          := "0.1.0-SNAPSHOT",
      scalaVersion     := Versions.Scala,
      buildInfoPackage := s"ch.timo_schmid.cmf.${name.value.replaceAll("-", ".")}",
      libraryDependencies ++= dependencies
    )

}
