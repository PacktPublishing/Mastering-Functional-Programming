package jvm

import cats._, cats.implicits._, cats.data._

object TailRecM extends App {
  implicit val simpleWriterMonad: Monad[SimpleWriter] = new Monad[SimpleWriter] {
    override def map[A, B](fa: SimpleWriter[A])(f: A => B): SimpleWriter[B] =
      fa.copy(value = f(fa.value))

    override def flatMap[A, B](fa: SimpleWriter[A])(f: A => SimpleWriter[B]): SimpleWriter[B] = {
      val res = f(fa.value)
      SimpleWriter(fa.log ++ res.log, res.value)
    }

    override def pure[A](a: A): SimpleWriter[A] = SimpleWriter(Nil, a)
    
    // @annotation.tailrec
    // override def tailRecM[A, B](a: A)(f: A => SimpleWriter[Either[A,B]]): SimpleWriter[B] =
    //   f(a).flatMap {
    //     case Left  (a1) => tailRecM(a1)(f)
    //     case Right(res) => pure(res)
    //   }

    @annotation.tailrec
    override def tailRecM[A, B](a: A)(f: A => SimpleWriter[Either[A,B]]): SimpleWriter[B] = {
      val next = f(a)
      next.value match {
        case Left  (a1) => tailRecM(a1)(f)
        case Right(res) => pure(res)
      }
    }
  }

  Monad[SimpleWriter].tailRecM[Int, Unit](0) { a => Monad[SimpleWriter].pure(Left(a))}
}