package jvm.server
package db

import cats._, cats.implicits._, cats.effect._
import doobie._, doobie.implicits._

object infrastructure {
  implicit lazy val tr: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver"
    , s"jdbc:postgresql://${sys.env("POSTGRES_HOST")}:${sys.env("POSTGRES_PORT")}/postgres"
    , "postgres", "")
}
