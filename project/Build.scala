import org.typelevel.sbt.tpolecat.TpolecatPlugin
import sbt.*
import sbtbuildinfo.BuildInfoPlugin

object Build {

  type SbtModules  = Seq[ModuleID]
  type SbtSettings = Seq[Setting[?]]

  val Root: Project =
    Project("cmf", file("."))
      .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
      .in(file("."))
      .settings(Settings.Root)

  def service(name: String, settings: SbtSettings): Project =
    project(name, settings ++ Seq(Keys.name := name))

  def module(name: String, settings: SbtSettings): Project =
    project(s"module/$name", settings ++ Seq(Keys.name := s"module-$name"))

  private def project(name: String, settings: SbtSettings): Project =
    Project(name.replaceAll("/", "-"), file(name))
      .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
      .settings(settings)

  private def base(dir: String): File =
    file(dir.replaceAll("-", "/"))

}
