package jvm

import scala.util.{ Try, Success, Failure }


object Division extends App {
  def imperativeDivision(n1: Double, n2: Double): Double =
    if (n2 == 0) throw new RuntimeException("Division by zero!")
    else n1 / n2

  def functionalDivision(n1: Double, n2: Double): Try[Double] =
    if (n2 == 0) Failure(new RuntimeException("Division by zero!"))
    else Success(n1 / n2)

  try imperativeDivision(1, 0)  // Exception is thrown
  catch { case x: RuntimeException => x.printStackTrace() }

  println(functionalDivision(1, 0))  // Failure
}
