package jvm

import cats.Monad, cats.syntax.all._

object AdditionMonadic extends App {
  def add[F[_]](a: Double, b: Double)(implicit M: Monad[F], L: Logging[F]): F[Double] =
    for {
      _   <- L.log(s"Adding $a to $b")
      res  = a + b
      _   <- L.log(s"The result of the operation is $res")
    } yield res

  println(add[SimpleWriter](1, 2))  // SimpleWriter(List(Adding 1.0 to 2.0, The result of the operation is 3.0),3.0)

}

case class SimpleWriter[A](log: List[String], value: A)

object SimpleWriter {
  // Wraps a value into SimpleWriter
  def pure[A](value: A): SimpleWriter[A] =
    SimpleWriter(Nil, value)

  // Wraps a log message into SimpleWriter
  def log(message: String): SimpleWriter[Unit] =
    SimpleWriter(List(message), ())

  implicit val monad: Monad[SimpleWriter] = new Monad[SimpleWriter] {
    override def map[A, B](fa: SimpleWriter[A])(f: A => B): SimpleWriter[B] =
      fa.copy(value = f(fa.value))

    override def flatMap[A, B](fa: SimpleWriter[A])(f: A => SimpleWriter[B]): SimpleWriter[B] = {
      val res = f(fa.value)
      SimpleWriter(fa.log ++ res.log, res.value)
    }

    override def pure[A](a: A): SimpleWriter[A] = SimpleWriter(Nil, a)
    
    override def tailRecM[A, B](a: A)(f: A => SimpleWriter[Either[A,B]]): SimpleWriter[B] = ???
  }

}


trait Logging[F[_]] {
  def log(msg: String): F[Unit]
}

object Logging {
  implicit val writerLogging: Logging[SimpleWriter] = new Logging[SimpleWriter] {
    def log(msg: String) = SimpleWriter.log(msg)
  }
}
