package jvm

import cats._, cats.syntax.all._, cats.instances.list._

object TraverseExample extends App {
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

  implicit val monoidDouble: Monoid[Double] = new Monoid[Double] {
    def combine(x1: Double, x2: Double): Double = x1 + x2
    def empty: Double = 0
  }


  def combineComputationsFold(f1: List[Fx[Double]]): Fx[Double] =
    f1.traverse(identity).map { lst =>
      lst.foldLeft(0D) { (runningSum, next) => runningSum + next } }

  def combineComputations(f1: List[Fx[Double]]): Fx[Double] =
    f1.traverse(identity).map(_.combineAll)


  val samples: List[Fx[Double]] =
    (1 to 5).toList.map { x => Right(x.toDouble) }

  val samplesErr: List[Fx[Double]] =
    (1 to 5).toList.map {
      case x if x % 2 == 0 => Left(List(s"$x is not a multiple of 2"))
      case x => Right(x.toDouble)
    }

  println(combineComputationsFold(samples))     // Right(15.0)
  println(combineComputationsFold(samplesErr))  // Left(List(2 is not a multiple of 2, 4 is not a multiple of 2))
  println(combineComputations(samples))         // Right(15.0)
  println(combineComputations(samplesErr))      // Left(List(2 is not a multiple of 2, 4 is not a multiple of 2))

}
