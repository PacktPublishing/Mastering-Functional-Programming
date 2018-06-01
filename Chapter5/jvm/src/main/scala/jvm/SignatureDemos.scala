package jvm


object SignatureDemos extends App {
  class Dummy(val id: Int) {
    val f: Int => String = x => s"Number: $x; Dummy: $id"
  }
  
  // val f: Int => String = x => s"Number: $x; Dummy: $id"  // No `id` in scope, does not compile
  
  val f1: Dummy => (Int => String) = d => (x => s"Number: $x; Dummy: ${d.id}")
  val f2: Int => (Dummy => String) = x => (d => s"Number: $x; Dummy: ${d.id}")

  println(new Dummy(1).f(2))    // Number: 2; Dummy: 1
  println(f1(new Dummy(1))(2))  // Number: 2; Dummy: 1
  println(f2(2)(new Dummy(1)))  // Number: 2; Dummy: 1
}
