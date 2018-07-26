package jvm

import scala.concurrent.{ Future, Await }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import cats._, cats.implicits._

trait Capabilities[F[_]] {
  def resource(name: String): F[String]
  def notify(target: String, text: String): F[Unit]
}

object TaglessFinalExample extends App {
  implicit val capabilities: Capabilities[Future] = new Capabilities[Future] {
    import java.io.File
    import org.apache.commons.io.FileUtils

    def resource(name: String): Future[String] =
      Future { FileUtils.readFileToString(new File(name), "utf8") }

    def notify(target: String, text: String): Future[Unit] =
      Future { println(s"Notifying $target: $text") }
  }

  implicit val anotherEnvironmentCapabilities: Capabilities[Future] = new Capabilities[Future] {
    def resource(name: String): Future[String] = ???
    def notify(target: String, text: String): Future[Unit] = ???
  }

  implicit val logMonad: Monad[Future] = new Monad[Future] {
    def flatMap[A, B](fa: Future[A])(f: (A) ⇒ Future[B]): Future[B] =
      fa.flatMap { x =>
        println(s"Trace of the Future's result: $x")
        f(x) }
    
    def pure[A](x: A): Future[A] = Future(x)

    def tailRecM[A, B](a: A)(f: (A) ⇒ Future[Either[A, B]]): Future[B] = ???
  }

  def income[F[_]](implicit M: Monad[F], C: Capabilities[F]): F[Unit] =
    for {
      contents <- C.resource("sales.csv")
      total = contents
        .split("\n").toList.tail  // Collection of lines, drop the CSV header
        .map { _.split(",").toList match  // List[Double] - prices of each of the entries
          { case name :: price :: Nil => price.toDouble }
        }
        .sum
      _ <- C.notify("admin@shop.com", s"Total income made today: $total")
    } yield ()

  Await.result(income[Future](logMonad, capabilities), Duration.Inf)  // Block so that the application does not exit prematurely
}

object FacadeExample {
  trait Capabilities {
    def resource(name: String): String
    def notify(target: String, text: String): Unit
  }

  def income(c: Capabilities): Unit = {
    val contents = c.resource("sales.csv")
    val total = contents
      .split("\n").toList.tail  // Collection of lines, drop the CSV header
      .map { _.split(",").toList match  // List[Double] - prices of each of the entries
        { case name :: price :: Nil => price.toDouble }
      }
      .sum
    c.notify("admin@shop.com", s"Total income made today: $total")
  }
}
