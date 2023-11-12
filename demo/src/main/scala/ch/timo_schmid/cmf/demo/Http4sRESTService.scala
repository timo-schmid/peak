package ch.timo_schmid.cmf.demo

import cats.Applicative
import cats.Monad
import cats.Show
import cats.effect.Async
import cats.effect.Resource
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.*
import ch.timo_schmid.cmf.core.api.Bootstrap.ServiceContext
import ch.timo_schmid.cmf.demo.CmsConfig.Http
import ch.timo_schmid.cmf.demo.Storages.Params
import ch.timo_schmid.cmf.module.user.Group
import ch.timo_schmid.cmf.module.user.Group.GroupId
import ch.timo_schmid.cmf.module.user.User
import ch.timo_schmid.cmf.module.user.User.UserId
import ch.timo_schmid.cmf.rest.http4s.REST
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.Server

class Http4sRESTService(server: Server):

  def address: SocketAddress[IpAddress] =
    server.address

object Http4sRESTService:

  given servicesProvider[F[_]: Async: Monad]
      : ServicesProvider[F, ServiceContext[F, CmsConfig, NoClients], Http4sRESTService] =
    context => {
      for
        storageParams <- storagesParams[F](context)
        storages      <- Storages.provider[F].create(storageParams)
        http4s        <- httpServer(context.config.http)(storages)
      yield Http4sRESTService(http4s)
    }

  private def storagesParams[F[_]: Applicative](
      context: ServiceContext[F, CmsConfig, NoClients]
  ): Resource[F, Params[F]] =
    Resource.pure(Storages.Params[F](context.config.db, context.loggerProvider))

  private def httpServer[F[_]: Async](httpConfig: Http)(storages: Storages[F]) =
    given Storage[F, Group, GroupId] = storages.group
    given Storage[F, User, UserId]   = storages.user
    EmberServerBuilder
      .default[F]
      .withHost(httpConfig.host)
      .withPort(httpConfig.port)
      .withHttpApp(
        Router(
          "/api/groups" -> REST[Group].routes[F, GroupId],
          "/api/users"  -> REST[User].routes[F, UserId]
        ).orNotFound
      )
      .build

  given showHttp4sRESTService: Show[Http4sRESTService] =
    (http4RESTService: Http4sRESTService) =>
      s"Http4s REST Service running at ${http4RESTService.address.show}"

  given showSocketAddress[A <: Host: Show]: Show[SocketAddress[A]] =
    (socketAddress: SocketAddress[A]) => s"[${socketAddress.host.show}]${socketAddress.port.show}"

  given showIpAddress: Show[IpAddress] =
    (ipAddress: IpAddress) => ipAddress.toString

  given showPort: Show[Port] =
    (port: Port) => port.toString
