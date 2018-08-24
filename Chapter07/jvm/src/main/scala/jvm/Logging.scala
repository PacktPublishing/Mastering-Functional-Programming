package jvm

object LoggingWriter extends App {
  import SimpleWriter.log

  def add(a: Double, b: Double): SimpleWriter[Double] =
    for {
      _   <- log(s"Adding $a to $b")
      res  = a + b
      _   <- log(s"The result of the operation is $res")
    } yield res

  println(add(1, 2))  // SimpleWriter(List(Adding 1.0 to 2.0, The result of the operation is 3.0),3.0)
}

object LoggingIO extends App {
  import IO.log

  def addIO(a: Double, b: Double): IO[Double] =
    for {
      _   <- log(s"Adding $a to $b")
      res  = a + b
      _   <- log(s"The result of the operation is $res")
    } yield res

  addIO(1, 2).operation()
  // Outputs:
  // Writing message to log file: Adding 1.0 to 2.0
  // Writing message to log file: The result of the operation is 3.0
}

object LoggingAbstract extends App {
  // Does not compile
  // def add[F[_]](a: Double, b: Double): F[Double] =
  //   for {
  //     _   <- log(s"Adding $a to $b")
  //     res  = a + b
  //     _   <- log(s"The result of the operation is $res")
  //   } yield res

  import Monad.Ops

  def add[F[_]](a: Double, b: Double)(implicit M: Monad[F], L: Logging[F]): F[Double] =
    for {
      _   <- L.log(s"Adding $a to $b")
      res  = a + b
      _   <- L.log(s"The result of the operation is $res")
    } yield res

  println(add[SimpleWriter](1, 2))  // SimpleWriter(List(Adding 1.0 to 2.0, The result of the operation is 3.0),3.0)

  println(add[IO](1, 2).operation())
  // Outputs:
  // Writing message to log file: Adding 1.0 to 2.0
  // Writing message to log file: The result of the operation is 3.0
  // 3.0
}

trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
}

object Monad {
  implicit class Ops[F[_], A](fa: F[A])(implicit m: Monad[F]) {
    def map[B](f: A => B): F[B] = m.map(fa)(f)
    def flatMap[B](f: A => F[B]): F[B] = m.flatMap(fa)(f)
  }

  implicit val writerMonad: Monad[SimpleWriter] = new Monad[SimpleWriter] {
    def pure[A](a: A): SimpleWriter[A] =
      SimpleWriter.pure(a)

    def map[A, B](fa: SimpleWriter[A])(f: A => B): SimpleWriter[B] =
      fa.map(f)

    def flatMap[A, B](fa: SimpleWriter[A])(f: A => SimpleWriter[B]): SimpleWriter[B] =
      fa.flatMap(f)
  }

  implicit val ioMonad: Monad[IO] = new Monad[IO] {
    def pure[A](a: A): IO[A] =
      IO.suspend(a)

    def map[A, B](fa: IO[A])(f: A => B): IO[B] =
      fa.map(f)

    def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
      fa.flatMap(f)
  }
}

trait Logging[F[_]] {
  def log(msg: String): F[Unit]
}

object Logging {
  implicit val writerLogging: Logging[SimpleWriter] = new Logging[SimpleWriter] {
    def log(msg: String) = SimpleWriter.log(msg)
  }

  implicit val ioLogging: Logging[IO] = new Logging[IO] {
    def log(msg: String) = IO.log(msg)
  }
}


case class IO[A](operation: () => A) {
  def flatMap[B](f: A => IO[B]): IO[B] =
    IO.suspend { f(operation()).operation() }

  def map[B](f: A => B): IO[B] =
    IO.suspend { f(operation()) }
}

object IO {
  def suspend[A](op: => A): IO[A] = IO(() => op)

  def log(str: String): IO[Unit] =
    IO.suspend { println(s"Writing message to log file: $str") }
}

case class SimpleWriter[A](log: List[String], value: A) {
  def flatMap[B](f: A => SimpleWriter[B]): SimpleWriter[B] = {
    val wb: SimpleWriter[B] = f(value)
    SimpleWriter(log ++ wb.log, wb.value)
  }

  def map[B](f: A => B): SimpleWriter[B] =
    SimpleWriter(log, f(value))
}

object SimpleWriter {
  // Wraps a value into SimpleWriter
  def pure[A](value: A): SimpleWriter[A] =
    SimpleWriter(Nil, value)

  // Wraps a log message into SimpleWriter
  def log(message: String): SimpleWriter[Unit] =
    SimpleWriter(List(message), ())
}
