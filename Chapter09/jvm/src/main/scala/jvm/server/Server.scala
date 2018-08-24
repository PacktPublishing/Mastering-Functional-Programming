package jvm.server

import scala.concurrent.ExecutionContext.Implicits.global

import cats._, cats.implicits._, cats.effect._
import org.http4s._, org.http4s.dsl.io._, org.http4s.server.blaze._
import io.circe.generic.auto._


object Server extends App with CirceImplicits {
  def noAuthCookieError =
    new RuntimeException("Please include the auth cookie")

  def all = (
      createCustomer
  <+> placeOrder
  <+> listOrders
  <+> listGoods)

  def success[T](payload: T): Map[String, T] =
    Map("success" -> payload)

  def createCustomer = HttpService[IO] {
    case req @ POST -> Root / "customer" =>
      for {
        reqBody <- req.as[Customer]
        id      <- db.customer.create(reqBody)
        resp    <- Ok(success(id.toString))
      } yield resp
    }

  def placeOrder = HttpService[IO] {
    case req @ POST -> Root / "order" =>
      for {
        cookieHeader <-
          headers.Cookie.from(req.headers).map(IO.pure).getOrElse(
            IO.raiseError(noAuthCookieError))
        jsonBody <- req.as[Map[String, Int]]
        cookie <- cookieHeader.values.toList
          .find(_.name == "shop_customer_id").map(IO.pure).getOrElse(
            IO.raiseError(noAuthCookieError))
        uId = cookie.content

        oId   <- db.order.create(Order(good = jsonBody("good"), customer = uId.toInt))
        order <- db.order.get(oId)
        resp  <- Ok(success(order))
      } yield resp
  }

  def listOrders = HttpService[IO] {
    case req @ GET -> Root / "order" =>
      db.order.list.flatMap(Ok(_))
  }

  def listGoods = HttpService[IO] {
    case req @ GET -> Root / "good" =>
      db.good.list.flatMap(Ok(_))
  }


  BlazeBuilder[IO]
    .bindHttp(8888, "0.0.0.0")
    .mountService(all, "/")
    .serve.compile.drain.unsafeRunSync()
}
