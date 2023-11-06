package ch.timo_schmid.cmf.api

trait Service[F[_], Request, Response, Error] {

  def handle(request: Request): F[Response | Error]

}
