package jvm

object Division extends App {

  def division(n1: Double, n2: Double): Either[String, Double] =
    if (n2 == 0) Left("Division by zero!")
    else Right(n1 / n2)

  println(division(1, 0))  // Left("Division by Zero")
  println(division(2, 2))  // Right(1.0)
}
