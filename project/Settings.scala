import sbt.*
import sbt.Keys.*
import sbtbuildinfo.BuildInfoKeys.*

object Settings {

  type SbtSettings = Seq[Setting[?]]

  val Common: SbtSettings = Seq(
    version          := "0.1.0-SNAPSHOT",
    scalaVersion     := Versions.Scala,
    libraryDependencies ++= Dependencies.All,
    buildInfoPackage := s"ch.timo_schmid.cmf.${name.value.replaceAll("-", ".")}"
  )

  val Cms: SbtSettings = Common ++ Seq(
    name := "cms"
  )

  object Module {

    object Core {

      val Api: SbtSettings = Common ++ Seq(
        name := "module-core-api"
      )

    }

    object Db {

      val Api: SbtSettings = Common ++ Seq(
        name := "module-db-api"
      )

      val Doobie: SbtSettings = Common ++ Seq(
        name := "module-db-doobie"
      )

    }

    val Di: SbtSettings = Common ++ Seq(
      name := "module-di"
    )

    object Log {

      val Slf4j: SbtSettings = Common ++ Seq(
        name := "module-log-slf4j"
      )

    }

    val User: SbtSettings = Common ++ Seq(
      name := "module-user"
    )

    object Rest {

      val Api: SbtSettings = Common ++ Seq(
        name := "module-rest-api"
      )

      val Http4s: SbtSettings = Common ++ Seq(
        name := "module-rest-http4s"
      )

    }

  }

  val Root: SbtSettings = Common ++ Seq(
    name := "cmf"
  )

}
