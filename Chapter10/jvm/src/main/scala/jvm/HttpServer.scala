package jvm

import scala.language.reflectiveCalls

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import cats._, cats.implicits._, cats.data._


trait HttpServer {
  type Token
  type Request <: { def token: Token }
  type Response
  type Post

  def allPosts(): List[Post]

  def respond[A](a: A): Response

  def authenticate(token: Token): Boolean

  def handle(request: Request): Response = {
    val userToken: Token = request.token
    val authenticated: Boolean = authenticate(userToken)

    if (authenticated) {
      val posts: List[Post] = allPosts()
      respond(posts)
    }
    else respond("You are not authorized to perform this action")
  }
}

trait HttpServerAsync {
  type Token
  type Request <: { def token: Token }
  type Response
  type Post

  def allPosts(): Future[List[Post]]

  def respond[A](a: A): Response

  def authenticate(token: Token): Future[Boolean]

  def handle(request: Request): Future[Response] =
    for {
      authenticated <- authenticate(request.token)
      response <- 
        if (authenticated) allPosts.map(respond)
        else Future { respond("You are not authorized to perform this action") }
    } yield response
}

trait HttpServerStackingNaive {
  type Token
  type Request <: { def token: Token }
  type Response
  type Post

  def allPosts(): Future[Either[String, List[Post]]]

  def respond[A](a: A): Response

  def authenticate(token: Token): Future[Either[String, Boolean]]

  def handle(request: Request): Future[Either[String, Response]] =
    authenticate(request.token).flatMap {
      case Right(authenticated) if authenticated =>
        allPosts.map { eitherPosts =>
          eitherPosts.map(respond)
        }

      case Right(authenticated) if !authenticated =>
        Future { Left("You are not authorized to perform this action") }

      case Left(error) => Future(Left(error))
    }
}

trait HttpServerTransformers {
  type Token
  type Request <: { def token: Token }
  type Response
  type Post

  type Config
  type Ef[A] = ReaderT[EitherT[Future, String, ?], Config, A]

  def allPosts(): EitherT[Future, String, List[Post]]

  def respond[A](a: A): Response

  def authenticate(token: Token): EitherT[Future, String, Boolean]

  def handle(request: Request): EitherT[Future, String, Response] =
    for {
      authenticated <- authenticate(request.token)
        .ensure("You are not authorized to perform this action")(identity)
      posts <- allPosts()
      response = respond(posts)
    } yield response
}
