package jvm

import cats._, cats.syntax.all._

object MonoidEither extends App {

  implicit def applicative[L: Monoid]: Applicative[Either[L, ?]] = new Applicative[Either[L, ?]] {
    override def ap[A, B](ff: Either[L, A => B])(fa: Either[L, A]): Either[L, B] = (ff, fa) match {
      case (Right(f), Right(a)) => Right(f(a))
      case (Left(e1), Left(e2)) => Left(e1 |+| e2)
      case (Left(e), _) => Left(e)
      case (_, Left(e)) => Left(e)
    }

    override def pure[A](a: A): Either[L, A] = Right(a)
  }

  val semigroupInt: Semigroup[Int] = new Semigroup[Int] {
    override def combine(a: Int, b: Int) = a + b
  }

  def monoidInt: Monoid[Int] = new Monoid[Int] {
    override def combine(a: Int, b: Int) = a + b
    override def empty = 0
  }

  implicit def monoidIntMult: Monoid[Int] = new Monoid[Int] {
    override def combine(a: Int, b: Int) = a * b
    override def empty = 1
  }

  println(2 |+| 3)  // 6
  println(2 combine 3)  // 6

  implicit val listMonoid: MonoidK[List] = new MonoidK[List] {
    override def combineK[A](a1: List[A], a2: List[A]): List[A] =
      a1 ++ a2

    override def empty[A] = Nil
  }
}
