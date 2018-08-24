package jvm.server

import cats._, cats.effect._
import org.http4s._
import io.circe._


trait CirceImplicits {

  implicit def jsonEncoderOfImpl[
    F[_]: Applicative
  , A   : Encoder](implicit ee: EntityEncoder[F, String]): EntityEncoder[F, A]
  = org.http4s.circe.jsonEncoderOf[F, A](ee, Applicative[F], Encoder[A])

  implicit def jsonOfImpl[
    F[_]: Effect
  , A   : Decoder]
  = org.http4s.circe.jsonOf[F, A]

}
