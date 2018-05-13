package jvm.mapping

object MappingInfix extends App{
  val numbers = List(1, 2, 3)
  val result  = numbers map (n => n * n)
  println(result)
}
