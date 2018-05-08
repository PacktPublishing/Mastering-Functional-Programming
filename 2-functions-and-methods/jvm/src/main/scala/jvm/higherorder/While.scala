package jvm

object While {
  def main(args: Array[String]): Unit = {
    var i = 0
    while (i < 5) {
      println(s"Printing from built-in while loop. Iteration: $i")
      i += 1
    }

    var j = 0
    whileDiy (j < 5) {
      println(s"Printing from custom while loop. Iteration: $j")
      j += 1
    }
  }

  @annotation.tailrec
  def whileDiy(predicate: => Boolean)(body: => Unit): Unit =
    if (predicate) {
      body
      whileDiy(predicate)(body)
    }
}
