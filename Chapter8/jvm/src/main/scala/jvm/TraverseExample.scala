package jvm

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

  def combineComputations(f1: List[Fx[Double]]): Fx[Double] =
    f1.traverse(identity).combineAll

}
