package ch.timo_schmid.cmf.core.api

trait Client[F[_], Request, Response, Error] {

  def call(request: Request): F[Response | Error]

}
