package jvm.effect

import scala.util.Try

object TryDemo extends App {
  def division(n1: Double, n2: Double): Double =
    if (n2 == 0) throw new RuntimeException("Division by zero!")
    else n1 / n2

  def pureDivision(n1: Double, n2: Double): Try[Double] =
    Try { division(n1, n2) }

  println(pureDivision(1, 0))  // Failure(java.lang.RuntimeException: Division by zero!)

  println(division(1, 0))  // throws java.lang.RuntimeException: Division by zero!
}
