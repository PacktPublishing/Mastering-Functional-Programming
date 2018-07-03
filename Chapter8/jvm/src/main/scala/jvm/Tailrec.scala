package jvm

object Tailrec extends App {
  def factorial(n: Int): Int =
    if (n <= 0) 1
    else n * factorial(n - 1)

  println(factorial(5))  // 120

  def factorialTailrec(n: Int, accumulator: Int = 1): Int =
    if (n <= 0) accumulator
    else factorialTailrec(n - 1, n * accumulator)

  println(factorialTailrec(5))  // 120
}
