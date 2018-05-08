package jvm

object MainScala {
  def main(args: Array[String]): Unit = {
    // Source collection
    val employees = List(
      "Ann"
    , "John"
    , "Amos"
    , "Jack")

    // Those employees with their names starting with 'A'
    val result = employees.filter { e => e(0) == 'A' }
    println(result)
  }
}
