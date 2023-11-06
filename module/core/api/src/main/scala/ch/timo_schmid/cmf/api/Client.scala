package ch.timo_schmid.cmf.api

trait Client[F[_], Request, Response, Error] {

  def call(request: Request): F[Response | Error]

}
