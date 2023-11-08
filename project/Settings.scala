import Build.SbtSettings
import sbt.*
import sbt.Keys.*
import sbtbuildinfo.BuildInfoKeys.*

object Settings {

  val Common: SbtSettings = Seq(
    version          := "0.1.0-SNAPSHOT",
    scalaVersion     := Versions.Scala,
    libraryDependencies ++= Dependencies.All,
    buildInfoPackage := s"ch.timo_schmid.cmf.${name.value.replaceAll("-", ".")}"
  )

  val Demo: SbtSettings = Common

  val IntegrationTests: SbtSettings = Common ++ Seq(
    libraryDependencies ++= Dependencies.IntegrationTests
  )

  object Module {

    object Config {

      val Pureconfig: SbtSettings = Common ++ Seq(
        libraryDependencies ++= Dependencies.Pureconfig
      )

    }

    object Core {

      val Api: SbtSettings = Common

      val Entity: SbtSettings = Common

    }

    object Db {

      val Api: SbtSettings = Common

      val Doobie: SbtSettings = Common

    }

    val Di: SbtSettings = Common

    object Log {

      val Slf4j: SbtSettings = Common

    }

    val User: SbtSettings = Common

    object Rest {

      val Api: SbtSettings = Common

      val Http4s: SbtSettings = Common

    }

  }

  val Root: SbtSettings = Common

}
