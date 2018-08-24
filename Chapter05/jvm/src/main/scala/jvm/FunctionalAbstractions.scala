package jvm

import scala.util.{ Try, Success, Failure }


object FunctionalAbstractions extends App {
  def divide(n1: Double, n2: Double): Try[Double] =
    if (n2 == 0) Failure(new RuntimeException("Division by zero!"))
    else Success(n1 / n2)

  def f1Match(x: Double): Try[Double] =
    divide(2, x) match {
      case Success(res) => Success(res + 3)
      case f@Failure(_) => f 
    }

  def f1Map(x: Double): Try[Double] =
    divide(2, x).map(r => r + 3)

  def f2Match(x: Double, y: Double): Try[Double] =
    divide(2, x) match {
      case Success(r1) => divide(r1, y) match {
        case Success(r2) => Success(r2 + 3)
        case f@Failure(_) => f
      }
      case f@Failure(_) => f
    }

  def f2FlatMap(x: Double, y: Double): Try[Double] =
    divide(2, x).flatMap(r1 => divide(r1, y))
      .map(r2 => r2 + 3)

  println(f1Match(2))  // 4.0
  println(f1Match(0))  // Failure

  println(f1Map(2))  // 4.0
  println(f1Map(0))  // Failure

  println(f2Match(2, 2))  // 3.5
  println(f2Match(2, 0))  // Failure

  println(f2FlatMap(2, 2))  // 3.5
  println(f2FlatMap(2, 0))  // Failure
}
