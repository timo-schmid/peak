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

  val Demo: SbtSettings = Common ++ Seq(
    name := "demo"
  )

  object Module {

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
