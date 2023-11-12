import Build.*

lazy val `demo` =
  service("demo", Settings.Demo)
    .dependsOn(
      `module-config-pureconfig`,
      `module-db-doobie`,
      `module-rest-api`,
      `module-log-slf4j`,
      `module-user`,
      `module-rest-http4s`
    )

lazy val `integration-tests` =
  service("integration-tests", Settings.IntegrationTests)
    .dependsOn(`demo`, `module-client-http4s`)

lazy val `module-client-http4s` =
  module("client-http4s", Settings.Module.Client.Http4s)
    .dependsOn(`module-codec-http4s-circe`, `module-core-entity`)

lazy val `module-codec-circe` =
  module("codec-circe", Settings.Module.Codec.Circe)

lazy val `module-codec-http4s-circe` =
  module("codec-http4s-circe", Settings.Module.Codec.Http4sCirce)
    .dependsOn(`module-codec-circe`)

lazy val `module-config-pureconfig` =
  module("config-pureconfig", Settings.Module.Config.Pureconfig)
    .dependsOn(`module-core-api`)

lazy val `module-core-api` =
  module("core-api", Settings.Module.Core.Api)

lazy val `module-core-entity` =
  module("core-entity", Settings.Module.Core.Entity)

lazy val `module-db-api` =
  module("db-api", Settings.Module.Db.Api)
    .dependsOn(`module-core-api`, `module-core-entity`)

lazy val `module-db-doobie` =
  module("db-doobie", Settings.Module.Db.Doobie)
    .dependsOn(`module-db-api`, `module-log-slf4j`)

lazy val `module-di` =
  module("di", Settings.Module.Di)

lazy val `module-log-slf4j` =
  module("log-slf4j", Settings.Module.Log.Slf4j)
    .dependsOn(`module-core-api`)

lazy val `module-user` =
  module("user", Settings.Module.User)
    .dependsOn(
      `module-db-doobie`,
      `module-rest-http4s`
    )

lazy val `module-rest-api` =
  module("rest-api", Settings.Module.Rest.Api)
    .dependsOn(`module-core-entity`)

lazy val `module-rest-http4s` =
  module("rest-http4s", Settings.Module.Rest.Http4s)
    .dependsOn(`module-codec-http4s-circe`, `module-core-api`, `module-rest-api`)

lazy val `root` =
  Root
    .aggregate(
      `demo`,
      `integration-tests`,
      `module-client-http4s`,
      `module-codec-circe`,
      `module-codec-http4s-circe`,
      `module-config-pureconfig`,
      `module-core-api`,
      `module-db-api`,
      `module-db-doobie`,
      `module-di`,
      `module-user`,
      `module-rest-api`,
      `module-rest-http4s`,
      `module-log-slf4j`
    )
