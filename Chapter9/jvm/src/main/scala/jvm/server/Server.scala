package jvm.server

import scala.concurrent.ExecutionContext.Implicits.global

import cats._, cats.implicits._, cats.effect._
import org.http4s._, org.http4s.dsl.io._, org.http4s.server.blaze._
import io.circe.generic.auto._


object Server extends App with CirceImplicits {
  def all = (
      createCustomer)
  // <+> placeOrder
  // <+> listOrders)

  def success(msg: String) = Map("success" -> msg)

  def createCustomer = HttpService[IO] {
    case req @ POST -> Root / "customer" =>
      for {
        reqBody <- req.as[Customer]
        id      <- db.customer.create(reqBody)
        resp    <- Ok(success(id.toString))
      } yield resp
    }


  BlazeBuilder[IO]
    .bindHttp(8888, "0.0.0.0")
    .mountService(all, "/")
    .serve.run.unsafeRunSync()
}
