package jvm

object FilterString extends App {
  println("Foo".filter(_ != 'o'))  // "F"
}
