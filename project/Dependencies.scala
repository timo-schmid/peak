import Build.SbtModules
import LibraryGroups.*
import sbt.*

object Dependencies {
  lazy val IntegrationTests: SbtModules =
    dependencies(Seq.empty, CatsEffectTesting ++ Http4sEmberClient ++ EmbeddedPostgres ++ Specs2)
  object Module {
    lazy val Client: SbtModules = dependencies(LibraryGroups.Http4sClient)
    object Codec  {
      lazy val Circe: SbtModules       = dependencies(LibraryGroups.Circe)
      lazy val Http4sCirce: SbtModules = dependencies(CirceFs2 ++ LibraryGroups.Http4sCirce)
    }
    object Core   {
      val Api: SbtModules    = dependencies(CatsEffect ++ Fs2 ++ Log4Cats)
      val Entity: SbtModules = dependencies(Cats ++ Shapeless)
    }
    object Db     {
      val Api: SbtModules    = dependencies(Fs2 ++ LibraryGroups.Doobie ++ LibraryGroups.Ip4s)
      val Doobie: SbtModules = dependencies(LibraryGroups.Doobie ++ Flyway, Specs2)
    }
    val Di: SbtModules = dependencies(CatsEffect, CatsEffectTesting ++ Specs2)
    object Config {
      lazy val Pureconfig: SbtModules = dependencies(LibraryGroups.Pureconfig)
    }
    object Rest   {
      val Api: SbtModules    = dependencies(Seq.empty, Specs2)
      val Http4s: SbtModules = dependencies(LibraryGroups.Circe ++ Http4sServer)
    }
    object Log    {
      val Slf4j: SbtModules = dependencies(Log4Cats ++ Logback)
    }
    val User: SbtModules = dependencies(Seq.empty, Specs2)
  }

  private def dependencies(compileTime: SbtModules, testTime: SbtModules = Seq.empty): SbtModules =
    compileTime ++ testTime.map(_ % Test)

}
