package ch.timo_schmid.cmf.cms

import cats.{Monad, Show}
import cats.effect.{Async, IO, Resource}
import cats.implicits.*
import ch.timo_schmid.cmf.api.ServicesProvider
import ch.timo_schmid.cmf.api.NoConfig
import ch.timo_schmid.cmf.db.Database
import ch.timo_schmid.cmf.db.doobie.databaseConnection
import ch.timo_schmid.cmf.module.user.*
import ch.timo_schmid.cmf.rest.http4s.RESTHttp4sRoutes
import com.comcast.ip4s.*
import doobie.ConnectionIO
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}

class Http4sRESTService(server: Server):

  def address: SocketAddress[IpAddress] =
    server.address

object Http4sRESTService:

  given servicesProvider[F[_]: Async: Monad]: ServicesProvider[F, CmsConfig, Http4sRESTService] =
    config => {
      databaseConnection[F].resource
        .flatMap { implicit givenDb =>
          EmberServerBuilder
            .default[F]
            .withHost(config.http4s.host)
            .withPort(config.http4s.port)
            .withHttpApp(
              Router(
                "/api/users" -> RESTHttp4sRoutes[F, UserId, User],
                "/api/users" -> RESTHttp4sRoutes[F, UserId, User]
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
