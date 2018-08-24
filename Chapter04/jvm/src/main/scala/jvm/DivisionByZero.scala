package jvm

object DivisionByZero extends App {
  def division(n1: Double, n2: Double): Double =
    if (n2 == 0) throw new RuntimeException("Division by zero!")
    else n1 / n2

  division(1, 0)
  println("This line will never be executed")
}
