lazy val `cms` = (project in file("cms"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Cms)
  .dependsOn(
    `module-db-doobie`,
    `module-rest-api`,
    `module-log-slf4j`,
    `module-user`,
    `module-rest-http4s`
  )

lazy val `module-core-api` = (project in file("module/core/api"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Core.Api)

lazy val `module-db-api` = (project in file("module/db/api"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Db.Api)
  .dependsOn(`module-core-api`)

lazy val `module-db-doobie` = (project in file("module/db/doobie"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Db.Doobie)
  .dependsOn(`module-db-api`)

lazy val `module-di` = (project in file("module/di"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Di)

lazy val `module-log-slf4j` = (project in file("module/log/slf4j"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Log.Slf4j)
  .dependsOn(`module-core-api`)

lazy val `module-user` = (project in file("module/user"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.User)
  .dependsOn(
    `module-db-doobie`,
    `module-rest-http4s`
  )

lazy val `module-rest-api` = (project in file("module/rest/api"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Rest.Api)

lazy val `module-rest-http4s` = (project in file("module/rest/http4s"))
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .settings(Settings.Module.Rest.Http4s)
  .dependsOn(`module-core-api`, `module-rest-api`)

lazy val `root` = project
  .enablePlugins(BuildInfoPlugin, TpolecatPlugin)
  .in(file("."))
  .settings(Settings.Root)
  .aggregate(
    `cms`,
    `module-core-api`,
    `module-db-api`,
    `module-db-doobie`,
    `module-di`,
    `module-user`,
    `module-rest-api`,
    `module-rest-http4s`,
    `module-log-slf4j`
  )
