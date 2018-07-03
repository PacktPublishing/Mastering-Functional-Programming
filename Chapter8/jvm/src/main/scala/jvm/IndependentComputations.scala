package jvm

import cats._, cats.syntax.all._

object IndependentComputations extends App {
  import cats.instances.all._

  type Fx[A] = Either[List[String], A]

  def combineComputations(f1: Fx[Double], f2: Fx[Double]): Fx[Double] =
    for {
      r1 <- f1
      r2 <- f2
    } yield r1 + r2

  val result = combineComputations(Right(1.0), Right(2.0))
  println(result)  // Right(3.0)

  val resultFirstFailed = combineComputations(
    Left(List("Division by zero")), Right(2.0))
  println(resultFirstFailed)  // Left(List(Division by zero))

  val resultSecondFailed = combineComputations(
    Right(1.0), Left(List("Null pointer encountered")))
  println(resultSecondFailed)  // Left(List(Null pointer encountered))

  val resultBothFailed = combineComputations(
    Left(List("Division by zero")), Left(List("Null pointer encountered")))
  println(resultBothFailed)  // Left(List(Division by zero))
}

object IndependentComputationsZip extends App {
  import cats.instances.all._

  type Fx[A] = Either[List[String], A]

  def zip[A, B](f1: Fx[A], f2: Fx[B]): Fx[(A, B)] = (f1, f2) match {
    case (Right(r1), Right(r2)) => Right((r1, r2))
    case (Left(e1), Left(e2)) => Left(e1 ++ e2)
    case (Left(e), _) => Left(e)
    case (_, Left(e)) => Left(e)
  }

  def combineComputations(f1: Fx[Double], f2: Fx[Double]): Fx[Double] =
    zip(f1, f2).map { case (r1, r2) => r1 + r2 }

  val result = combineComputations(Right(1.0), Right(2.0))
  println(result)  // Right(3.0)

  val resultFirstFailed = combineComputations(
    Left(List("Division by zero")), Right(2.0))
  println(resultFirstFailed)  // Left(List(Division by zero))

  val resultSecondFailed = combineComputations(
    Right(1.0), Left(List("Null pointer encountered")))
  println(resultSecondFailed)  // Left(List(Null pointer encountered))

  val resultBothFailed = combineComputations(
    Left(List("Division by zero")), Left(List("Null pointer encountered")))
  println(resultBothFailed)  // Left(List(Division by zero, Null pointer encountered))
}

object IndependentComputationsAp extends App {
  import cats.instances.all._

  type Fx[A] = Either[List[String], A]

  def ap[A, B](ff: Fx[A => B])(fa: Fx[A]): Fx[B] = (ff, fa) match {
    case (Right(f), Right(a)) => Right(f(a))
    case (Left(e1), Left(e2)) => Left(e1 ++ e2)
    case (Left(e), _) => Left(e)
    case (_, Left(e)) => Left(e)
  }

  def zip[A, B](f1: Fx[A], f2: Fx[B]): Fx[(A, B)] =
    ap[B, (A, B)](ap[A, B => (A, B)](Right { (a: A) => (b: B) => (a, b) })(f1))(f2)

  def combineComputations(f1: Fx[Double], f2: Fx[Double]): Fx[Double] =
    zip(f1, f2).map { case (r1, r2) => r1 + r2 }

  val result = combineComputations(Right(1.0), Right(2.0))
  println(result)  // Right(3.0)

  val resultFirstFailed = combineComputations(
    Left(List("Division by zero")), Right(2.0))
  println(resultFirstFailed)  // Left(List(Division by zero))

  val resultSecondFailed = combineComputations(
    Right(1.0), Left(List("Null pointer encountered")))
  println(resultSecondFailed)  // Left(List(Null pointer encountered))

  val resultBothFailed = combineComputations(
    Left(List("Division by zero")), Left(List("Null pointer encountered")))
  println(resultBothFailed)  // Left(List(Division by zero, Null pointer encountered))
}

object IndependentComputationsApplicative extends App {
  type Fx[A] = Either[List[String], A]

  implicit val applicative: Applicative[Fx] = new Applicative[Fx] {
    override def ap[A, B](ff: Fx[A => B])(fa: Fx[A]): Fx[B] = (ff, fa) match {
      case (Right(f), Right(a)) => Right(f(a))
      case (Left(e1), Left(e2)) => Left(e1 ++ e2)
      case (Left(e), _) => Left(e)
      case (_, Left(e)) => Left(e)
    }

    override def pure[A](a: A): Fx[A] = Right(a)
  }

  def combineComputations(f1: Fx[Double], f2: Fx[Double]): Fx[Double] =
    (f1, f2).mapN { case (r1, r2) => r1 + r2 }

  val result = combineComputations(Right(1.0), Right(2.0))
  println(result)  // Right(3.0)

  val resultFirstFailed = combineComputations(
    Left(List("Division by zero")), Right(2.0))
  println(resultFirstFailed)  // Left(List(Division by zero))

  val resultSecondFailed = combineComputations(
    Right(1.0), Left(List("Null pointer encountered")))
  println(resultSecondFailed)  // Left(List(Null pointer encountered))

  val resultBothFailed = combineComputations(
    Left(List("Division by zero")), Left(List("Null pointer encountered")))
  println(resultBothFailed)  // Left(List(Division by zero, Null pointer encountered))
}
