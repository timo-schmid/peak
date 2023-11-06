package ch.timo_schmid.cmf.demo

import cats.Monad
import cats.Show
import cats.effect.Async
import cats.implicits.*
import ch.timo_schmid.cmf.core.api.ServicesProvider
import ch.timo_schmid.cmf.db.DatabaseConfig
import ch.timo_schmid.cmf.db.doobie.databaseConnection
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

  given servicesProvider[F[_]: Async: Monad]: ServicesProvider[F, CmsConfig, Http4sRESTService] =
    config => {
      given databaseConfig: DatabaseConfig = config.db
      databaseConnection[F].resource
        .flatMap { implicit givenDb =>
          EmberServerBuilder
            .default[F]
            .withHost(config.http.host)
            .withPort(config.http.port)
            .withHttpApp(
              Router(
                "/api/users"  -> REST[User].routes[F, UserId],
                "/api/groups" -> REST[Group].routes[F, GroupId]
              ).orNotFound
            )
            .build
        }
        .map(Http4sRESTService(_))
    }

  given showHttp4sRESTService: Show[Http4sRESTService] =
    (http4RESTService: Http4sRESTService) =>
      s"Http4s REST Service running at ${http4RESTService.address.show}"

  given showSocketAddress[A <: Host: Show]: Show[SocketAddress[A]] =
    (socketAddress: SocketAddress[A]) => s"[${socketAddress.host.show}]${socketAddress.port.show}"

  given showIpAddress: Show[IpAddress] =
    (ipAddress: IpAddress) => ipAddress.toString

  given showPort: Show[Port] =
    (port: Port) => port.toString
