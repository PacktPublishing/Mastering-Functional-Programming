package jvm

import scala.language.postfixOps
import cats._, cats.implicits._, cats.effect._, cats.data._

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.duration._

object HelloWorld extends App {
  val hello = IO { println("Hello") }
  val world = IO { println("World") }
  (hello *> world).unsafeRunSync
}

trait Context {
  implicit val ec: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))
  
  def benchmark[A](io: IO[A]): IO[(A, Long)] =
    for {
      tStart <- Timer[IO].clockMonotonic(SECONDS)
      res    <- io
      tEnd   <- Timer[IO].clockMonotonic(SECONDS)
    } yield (res, tEnd - tStart)

  def benchmarkFlush[A](io: IO[A]): IO[Unit] =
    benchmark(io).map { case (res, time) =>
      println(s"Computed result $res in $time seconds") }
}

trait Asynchrony extends Context {
  def taskHeavy(prefix: String): IO[Nothing] =
    Monad[IO].tailRecM(0) { i => for {
      _ <- IO { println(s"${Thread.currentThread.getName}; $prefix: $i") }
      _ <- IO { Thread.sleep(1000) }
    } yield Left(i + 1) }

  def taskLight(prefix: String): IO[Nothing] =
    Monad[IO].tailRecM(0) { i => for {
      _ <- IO { println(s"${Thread.currentThread.getName}; $prefix: $i") }
      _ <- IO.sleep(1 second)
    } yield Left(i + 1) }

  def bunch(n: Int)(gen: String => IO[Nothing]): IO[List[Fiber[IO, Nothing]]] =
    (1 to n).toList.map(i => s"Task $i").traverse(gen(_).start)
}

object AsynchronyHeavy extends Asynchrony with App {
  (IO.shift *> bunch(1000)(taskHeavy)).unsafeRunSync }

object AsynchronyLight extends Asynchrony with App {
  (IO.shift *> bunch(1000)(taskLight)).unsafeRunSync }

trait Fibers extends Context {
  def sum(from: Int, to: Int): IO[Int] =
    Monad[IO].tailRecM((from, 0)) { case (i, runningTotal) =>
      if (i == to) IO.pure( Right(runningTotal + i) )
      else if (i > to) IO.pure( Right(runningTotal) )
      else for {
        _ <- IO { println(s"${Thread.currentThread.getName}: " +
          s"Running total from $from to $to, currently at $i: $runningTotal") }
        _ <- IO.sleep(500 milliseconds)
      } yield Left((i + 1, runningTotal + i)) }

  def sequential: IO[Int] =
    for {
      s1 <- sum(1 , 10)
      s2 <- sum(10, 20)
    } yield s1 + s2

  def sequentialTraverse: IO[Int] =
    List(sum(1, 10), sum(10, 20)).traverse(identity).map(_.sum)

  def parallel: IO[Int] =
    for {
      f1 <- sum(1 , 10).start
      f2 <- sum(10, 20).start
      s1 <- f1.join
      s2 <- f2.join
    } yield s1 + s2

  def cancelled: IO[Int] =
    for {
      f1 <- sum(1 , 5 ).start
      f2 <- sum(10, 20).start
      res <- f1.join
      _  <- f2.cancel
    } yield res
}

object FibersSequantial extends Fibers with App {
  benchmarkFlush(sequential).unsafeRunSync }

object SequentialTraverse extends Fibers with App {
  benchmarkFlush(sequentialTraverse).unsafeRunSync }

object FibersParallel extends Fibers with App {
  benchmarkFlush(parallel).unsafeRunSync }

object FibersCancelled extends Fibers with App {
  benchmarkFlush(cancelled).unsafeRunSync }


trait SyncVsAsync extends Context {
  def taskHeavy(name: String): Int = {
    Thread.sleep(1000)
    println(s"${Thread.currentThread.getName}: " +
      s"$name: Computed!")
    42
  }

  def sync(name: String): IO[Int] =
    IO { taskHeavy(name) }
  
  def async(name: String): IO[Int] =
    IO.async { cb =>
      new Thread(new Runnable { override def run =
        cb { Right(taskHeavy(name)) } }).start()
    }

  def bunch(n: Int)(gen: String => IO[Int]): IO[List[Int]] =
    (1 to n).toList.map(i => s"Task $i").traverse(gen(_).start)
      .flatMap(_.traverse(_.join))
}

object SyncVsAsyncSync extends SyncVsAsync with App {
  benchmarkFlush(IO.shift *> bunch(10)(sync)).unsafeRunSync }

object SyncVsAsyncAsync extends SyncVsAsync with App {
  benchmarkFlush(IO.shift *> bunch(10)(async)).unsafeRunSync }
