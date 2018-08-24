package jvm

object BlackBox extends App {
  val listMutable  : Seq[Int] = collection.mutable.ListBuffer[Int](1, 2, 3)
  val listImmutable: Seq[Int] = List(1, 2, 3)

  def blackBox(x: Seq[Int]): Unit = ???

  blackBox(listMutable)  // Anything could happen to listMutable here, because it is mutable
  blackBox(listImmutable) // No matter what happens, listImmutable remains the same, because it is immutable
}
