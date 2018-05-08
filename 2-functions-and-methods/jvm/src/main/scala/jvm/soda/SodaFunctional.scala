package jvm.soda

object MainScala {
  def main(args: Array[String]): Unit = {
    val sm = SodaMachine(2)

    val (sm1, can1) = insertCoin(sm)
    val (sm2, can2) = insertCoin(sm1)

    can2.drink()
    val (sm3, can3) = insertCoin(sm2)  // Out of soda cans
  }

  def insertCoin(sm: SodaMachine): (SodaMachine, SodaCan) =
    if (sm.cans > 0) (SodaMachine(sm.cans - 1, sm.coins + 1), new SodaCan)
    else throw new RuntimeException("Out of soda cans!")
}

case class SodaMachine(cans: Int, coins: Int = 0)
