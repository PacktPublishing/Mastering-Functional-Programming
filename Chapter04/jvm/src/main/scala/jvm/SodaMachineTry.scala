package jvm

import scala.util.Try

object SodaMachineTry extends App {
  class SodaCan

  var cans = 0
  def insertCoin(): Try[SodaCan] = Try {
    if (cans > 0) { cans -= 1; new SodaCan }
    else throw new RuntimeException("Out of soda cans!")
  }

  println(insertCoin())
}
