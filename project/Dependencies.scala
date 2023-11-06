import sbt.*

object Dependencies {

  val All = Seq(
    "org.tpolecat"  %% "doobie-core"         % Versions.Doobie,
    "org.tpolecat"  %% "doobie-hikari"       % Versions.Doobie,
    "org.tpolecat"  %% "doobie-postgres"     % Versions.Doobie,
    "org.typelevel" %% "cats-effect"         % Versions.CatsEffect,
    "org.flywaydb"   % "flyway-core"         % Versions.Flyway,
    "org.postgresql" % "postgresql"          % Versions.Postgresql,
    "org.http4s"    %% "http4s-dsl"          % Versions.Http4s,
    "org.http4s"    %% "http4s-circe"        % Versions.Http4s,
    "org.http4s"    %% "http4s-ember-server" % Versions.Http4s,
    "org.typelevel" %% "log4cats-core"       % Versions.Log4Cats,
    "org.typelevel" %% "log4cats-slf4j"      % Versions.Log4Cats,
    "org.typelevel" %% "shapeless3-deriving" % Versions.Shapeless,
    "ch.qos.logback" % "logback-classic"     % Versions.Logback,
    "io.circe"      %% "circe-core"          % Versions.Circe,
    "io.circe"      %% "circe-generic"       % Versions.Circe,
    "io.circe"      %% "circe-jawn"          % Versions.Circe,
    "io.circe"      %% "circe-numbers"       % Versions.Circe
    // "org.scalameta" %% "munit"               % Versions.Munit % Test
  ) ++ Seq(
    "org.specs2"     %% "specs2-core"                % Versions.Specs2,
    "org.specs2"     %% "specs2-scalacheck"          % Versions.Specs2,
    "org.scalacheck" %% "scalacheck"                 % Versions.ScalaCheck,
    "org.typelevel"  %% "cats-effect-testing-specs2" % Versions.CatsEffectTesting
  ).map(_ % Test)

}
