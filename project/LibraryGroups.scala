import Build.SbtModules
import sbt.*

object LibraryGroups {

  lazy val CatsEffect: SbtModules =
    Seq(
      "org.typelevel" %% "cats-effect" % Versions.CatsEffect
    )

  lazy val Circe: SbtModules =
    Seq(
      "io.circe" %% "circe-core"    % Versions.Circe,
      "io.circe" %% "circe-generic" % Versions.Circe,
      "io.circe" %% "circe-jawn"    % Versions.Circe,
      "io.circe" %% "circe-numbers" % Versions.Circe
    )

  lazy val Doobie: SbtModules =
    Seq(
      "org.tpolecat" %% "doobie-core"     % Versions.Doobie,
      "org.tpolecat" %% "doobie-hikari"   % Versions.Doobie,
      "org.tpolecat" %% "doobie-postgres" % Versions.Doobie
    )

  lazy val EmbeddedPostgres: SbtModules =
    Seq(
      "io.zonky.test"          % "embedded-postgres"              % Versions.EmbeddedPostgres,
      "io.zonky.test.postgres" % "embedded-postgres-binaries-bom" % Versions.Postgres
    )

  lazy val Flyway: SbtModules =
    Seq(
      "org.flywaydb" % "flyway-core" % Versions.Flyway
    )

  lazy val Http4s: SbtModules =
    Seq(
      "org.http4s" %% "http4s-dsl"          % Versions.Http4s,
      "org.http4s" %% "http4s-circe"        % Versions.Http4s,
      "org.http4s" %% "http4s-ember-server" % Versions.Http4s
    )

  lazy val Log4Cats: SbtModules =
    Seq(
      "org.typelevel" %% "log4cats-core"  % Versions.Log4Cats,
      "org.typelevel" %% "log4cats-slf4j" % Versions.Log4Cats
    )

  lazy val Logback: SbtModules =
    Seq(
      "ch.qos.logback" % "logback-classic" % Versions.Logback
    )

  lazy val Postgresql: SbtModules =
    Seq(
      "org.postgresql" % "postgresql" % Versions.PostgresqlDriver
    )

  lazy val Pureconfig: SbtModules =
    Seq(
      "com.github.pureconfig" %% "pureconfig-core"        % Versions.PureConfig,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.PureConfig,
      "com.github.pureconfig" %% "pureconfig-ip4s"        % Versions.PureConfig
    )

  lazy val Shapeless: SbtModules =
    Seq(
      "org.typelevel" %% "shapeless3-deriving" % Versions.Shapeless
    )

  lazy val Specs2: SbtModules =
    Seq(
      "org.specs2"     %% "specs2-core"                % Versions.Specs2,
      "org.specs2"     %% "specs2-scalacheck"          % Versions.Specs2,
      "org.scalacheck" %% "scalacheck"                 % Versions.ScalaCheck,
      "org.typelevel"  %% "cats-effect-testing-specs2" % Versions.CatsEffectTesting
    )

}
