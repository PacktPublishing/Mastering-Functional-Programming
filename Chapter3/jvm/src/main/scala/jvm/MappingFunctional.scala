package jvm.mapping

object MappingFunctional {
  def main(args: Array[String]): Unit = {
    val numbers = List(1, 2, 3)
    val result  = numbers.map(n => n * n)
    println(result)
  }
}
