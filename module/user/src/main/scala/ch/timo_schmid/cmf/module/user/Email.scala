package ch.timo_schmid.cmf.module.user

import cats._
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import io.circe._
import io.circe.Decoder.Result

case class Email(local: String, path: String)

object Email:

  def unapply(string: String): Option[Email] =
    string.split("@").toList match
      case local :: path :: Nil => Some(Email(local, path))
      case _                    => None

  def unsafeFromString(string: String): Email =
    unapply(string) match
      case Some(email) => email
      case None        => sys.error(s"Could not parse email: $string")

  given encoder: Encoder[Email] =
    (email: Email) => Json.fromString(email.show)

  given decoder: Decoder[Email] =
    new Decoder[Email]:

      override def apply(cursor: HCursor): Result[Email] =
        cursor.as[String].flatMap(decodeEmail(_, cursor))

      private def decodeEmail(string: String, cursor: HCursor): Result[Email] =
        unapply(string) match
          case Some(email) => email.asRight[DecodingFailure]
          case _           => decodingFailure(string, cursor).asLeft[Email]

      private def decodingFailure(
          string: String,
          cursor: HCursor
      ): DecodingFailure =
        DecodingFailure(s"Could not decode email: '$string'", cursor.history)

  given showEmail: Show[Email] =
    email => List(email.local, email.path).mkString("@")

  // FIXME: Improve error handling
  given readEmail: Read[Email] =
    Read[String].map[Email](unsafeFromString)

  given putEmail: Put[Email] =
    Put[String].tcontramap(_.show)
