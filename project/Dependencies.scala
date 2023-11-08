import Build.SbtModules
import LibraryGroups.*
import sbt.*

object Dependencies {

  lazy val All: SbtModules =
    CatsEffect ++
      Circe ++
      Doobie ++
      Flyway ++
      Http4s ++
      Log4Cats ++
      Logback ++
      Postgresql ++
      Shapeless ++
      Testing

  lazy val IntegrationTests: SbtModules =
    Specs2.map(_ % Test)

  lazy val Testing: SbtModules =
    Specs2.map(_ % Test)

}
